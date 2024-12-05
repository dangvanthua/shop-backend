package com.thuan.shop_backend.service.product;

import com.thuan.shop_backend.constant.OrderStatus;
import com.thuan.shop_backend.dto.request.ProdRecommendRequest;
import com.thuan.shop_backend.dto.request.ProductRequest;
import com.thuan.shop_backend.dto.request.ProductVariantRequest;
import com.thuan.shop_backend.dto.request.VariantAttributeRequest;
import com.thuan.shop_backend.dto.response.ProductResponse;
import com.thuan.shop_backend.entity.*;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.*;
import com.thuan.shop_backend.service.file.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.core.Instances;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository productVariantRepo;
    private final VariantAttributeRepository variantAttributeRepo;
    private final AttributeRepository attributeRepository;
    private final ProductImageRepository productImageRepository;

    private final IFileService fileService;
    private final FeatureService featureService;
    private final SimilarityService similarityService;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {

        Seller seller = sellerRepository.findById(productRequest.getSellerId())
                .orElseThrow(() -> new AppException(ErrorCode.SELLER_NOT_EXISTED));

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .seller(seller)
                .category(category)
                .isActive(false)
                .build();

        product = productRepository.save(product);

        return ProductResponse.fromProduct(product, null);
    }

    @Override
    @Transactional
    public void createVariant(
            long productId,
            List<ProductVariantRequest> productVariantRequests) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        for (ProductVariantRequest variantReq: productVariantRequests) {
            ProductVariant productVariant = ProductVariant.builder()
                    .product(product)
                    .sku(variantReq.getSku())
                    .price(variantReq.getPrice())
                    .stockQuantity(variantReq.getStockQuantity())
                    .build();
            productVariant = productVariantRepo.save(productVariant);

            for(VariantAttributeRequest attReq: variantReq.getAttributeRequests()) {

                Attribute attribute = attributeRepository.findById(attReq.getAttributeId())
                        .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_NOT_EXIST));

                VariantAttribute variantAttribute = VariantAttribute.builder()
                        .productVariant(productVariant)
                        .attribute(attribute)
                        .value(attReq.getValue())
                        .build();
                
                variantAttributeRepo.save(variantAttribute);
            }
        }
    }

    @Override
    @Transactional
    public void uploadProductImages(
            long productId,
            Map<String, String> productImageUrl,
            boolean isThumbnail) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        long existingThumbnailCount = productImageRepository.countByProductIdAndIsThumbnail(productId);

        if(isThumbnail && existingThumbnailCount > 0) {
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }

        List<ProductImage> existingProductImages = productImageRepository.findByProductId(productId);
        for (ProductImage existingImage : existingProductImages) {
            if (existingImage.getCloudinaryPublicId() != null) {
                fileService.deleteFile(existingImage.getCloudinaryPublicId());
            }
            productImageRepository.delete(existingImage);
        }

        productImageUrl.forEach((cloudinaryPublicId,imageUrl) -> {
            ProductImage productImage = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .cloudinaryPublicId(cloudinaryPublicId)
                    .isThumbnail(isThumbnail)
                    .build();

            productImageRepository.save(productImage);
        });
    }

    @Override
    public Page<ProductResponse> getProductByCategory(long categoryId, Pageable pageable) {

        boolean hasProducts = productRepository.existsProductsInCategory(categoryId);
        Page<Product> productPage = null;

        if (hasProducts) {
            productPage = productRepository.findProductsByCategoryId(categoryId, pageable);
        } else {
            List<Category> subcategories = categoryRepository.findSubcategories(categoryId);
            List<Long> subcategoryIds = subcategories.stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());

            productPage = productRepository.findProductsByCategoryIds(subcategoryIds, pageable);
        }

        List<Long> productIds = productPage.getContent()
                .stream()
                .map(Product::getId)
                .toList();

        List<ProductImage> images = productImageRepository.findByProductIds(productIds);

        Map<Long, List<ProductImage>> imagesGroupedByProduct = images.stream()
                .collect(Collectors.groupingBy(image -> image.getProduct().getId()));

        return productPage.map(product -> {
            List<ProductImage> productImages = imagesGroupedByProduct.getOrDefault(product.getId(), Collections.emptyList());
            return ProductResponse.fromProduct(product, productImages);
        });
    }

    @Override
    public List<ProductResponse> recommendProducts(long productId, int topN) {

        Product targetProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        List<Product> products = productRepository.findByCategoryId(targetProduct.getCategory().getId());
        List<ProdRecommendRequest> recommendRequests = products.stream()
                .map(product -> ProdRecommendRequest.builder()
                        .name(product.getName())
                        .description(product.getDescription())
                        .categoryName(product.getCategory().getName())
                        .price(product.getPrice())
                        .quantity(product.getQuantity())
                        .build())
                .toList();

        try {
            Instances features = featureService.prepareTFIDFeatures(recommendRequests);

            int targetIndex = findProductIndex(targetProduct);

            double[] similarities = similarityService.calculateSimilarities(features, targetIndex);

            return findTopNSimilarProducts(products, similarities, targetIndex, topN);
        } catch (Exception e) {
            throw new AppException(ErrorCode.FAILED_RECOMMEND);
        }
    }

    @Override
    public Page<ProductResponse> getFeatureProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productRepository.findProductsByTopSelling(OrderStatus.DELIVERED, pageable);

        if (productPage.isEmpty()) {
            productPage = productRepository.findAll(pageable);
        }

        List<Long> productIds = productPage.getContent()
                .stream()
                .map(Product::getId)
                .toList();

        List<ProductImage> images = productImageRepository.findByProductIds(productIds);

        Map<Long, List<ProductImage>> imagesGroupedByProduct = images.stream()
                .collect(Collectors.groupingBy(image -> image.getProduct().getId()));

        return productPage.map(product -> {
            List<ProductImage> productImages = imagesGroupedByProduct.getOrDefault(product.getId(), Collections.emptyList());
            return ProductResponse.fromProduct(product, productImages);
        });
    }

    private int findProductIndex(Product targetProduct) {
        return featureService.getProductIndex(targetProduct.getName());
    }

    private List<ProductResponse> findTopNSimilarProducts(
            List<Product> products, double[] similarities, int targetIndex, int topN) {

        List<Integer> similarProductIndices = IntStream.range(0, similarities.length)
                .boxed()
                .filter(i -> i != targetIndex)
                .sorted((i1, i2) -> Double.compare(similarities[i2], similarities[i1]))
                .limit(topN)
                .toList();

        List<Product> similarProducts = similarProductIndices.stream()
                .map(products::get)
                .toList();

        List<Long> productIds = similarProducts.stream()
                .map(Product::getId)
                .toList();

        List<ProductImage> productImages = productImageRepository.findByProductIds(productIds);

        Map<Long, List<ProductImage>> imagesGroupedByProduct = productImages.stream()
                .collect(Collectors.groupingBy(image -> image.getProduct().getId()));

        return similarProducts.stream()
                .map(product -> {
                    List<ProductImage> images = imagesGroupedByProduct.getOrDefault(product.getId(), Collections.emptyList());
                    return ProductResponse.fromProduct(product, images);
                })
                .toList();
    }
}
