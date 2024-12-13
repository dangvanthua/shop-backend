package com.thuan.shop_backend.service.product;

import com.thuan.shop_backend.constant.OrderStatus;
import com.thuan.shop_backend.dto.request.product.ProdRecommendRequest;
import com.thuan.shop_backend.dto.request.product.ProductRequest;
import com.thuan.shop_backend.dto.response.product.ProductDetailResponse;
import com.thuan.shop_backend.dto.response.product.ProductImageResponse;
import com.thuan.shop_backend.dto.response.product.ProductResponse;
import com.thuan.shop_backend.dto.response.promotion.PromotionCodeResponse;
import com.thuan.shop_backend.dto.response.seller.SellerInfoResponse;
import com.thuan.shop_backend.entity.*;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.*;
import com.thuan.shop_backend.service.file.IFileService;
import com.thuan.shop_backend.service.product.recommend.FeatureService;
import com.thuan.shop_backend.service.product.recommend.SimilarityService;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ReviewRepository reviewRepository;
    private final ProductPromotionRepository promotionCodeRepository;

    private final IFileService fileService;
    private final FeatureService featureService;
    private final SimilarityService similarityService;

    @Override
    @Transactional
    public Product createProduct(ProductRequest productRequest) {

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

        return productRepository.save(product);
    }


    @Override
    @Transactional
    public void uploadProductImages(
            long productId,
            Map<String, String> productImageUrl,
            boolean isThumbnail) {

        // Lấy sản phẩm từ cơ sở dữ liệu
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Kiểm tra số lượng ảnh chính (isThumbnail)
        long existingThumbnailCount = productImageRepository.countByProductIdAndIsThumbnail(productId);

        // Nếu đang upload ảnh chính mới và đã có ảnh chính cũ, xóa ảnh chính cũ
        if (isThumbnail && existingThumbnailCount > 0) {
            // Tìm ảnh chính cũ và xóa nó
            ProductImage existingThumbnail = productImageRepository.findByProductIdAndIsThumbnail(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.UPLOAD_FILE_FAILED));

            // Xóa ảnh cũ trên Cloudinary nếu có
            if (existingThumbnail.getCloudinaryPublicId() != null) {
                fileService.deleteFile(existingThumbnail.getCloudinaryPublicId());
            }

            // Xóa ảnh chính cũ trong cơ sở dữ liệu
            productImageRepository.delete(existingThumbnail);
        }

        // Upload và lưu các ảnh mới (không xóa ảnh gallery cũ)
        productImageUrl.forEach((cloudinaryPublicId, imageUrl) -> {
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

    @Override
    public ProductDetailResponse getProductDetail(long productId) {
        // Lấy thông tin sản phẩm
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Lấy danh sách hình ảnh của sản phẩm, trả về danh sách rỗng nếu không có ảnh
        List<ProductImageResponse> imageResponses = Optional.ofNullable(productImageRepository.findByProductId(product.getId()))
                .orElse(Collections.emptyList())  // Trả về danh sách rỗng nếu không có hình ảnh
                .stream()
                .map(image -> ProductImageResponse.builder()
                        .imageUrl(image.getImageUrl())
                        .isThumbnail(image.getIsThumbnail())
                        .build())
                .collect(Collectors.toList());

        // Lấy thông tin người bán sản phẩm
        Seller seller = sellerRepository.findSellerByProductId(product.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SELLER_NOT_EXISTED));

        long totalProductsSold = sellerRepository.countProductsSoldBySeller(seller.getId());
        long totalReviews = reviewRepository.countReviewsBySellerId(seller.getId());

        // Tạo thông tin người bán
        SellerInfoResponse sellerInfo = SellerInfoResponse.fromSeller(
                seller, totalProductsSold, totalReviews);

        List<ProductPromotionCode> productPromotionCode = promotionCodeRepository
                .findByProductId(product.getId());

        List<PromotionCodeResponse> promotionCodeResponses = productPromotionCode.stream()
                .map(PromotionCodeResponse::fromProductPromotion)
                .toList();

        // Tạo response và trả về
        return ProductDetailResponse.fromProductDetail(
                product,
                imageResponses,
                promotionCodeResponses,
                sellerInfo);
    }

    @Override
    @Transactional
    public Product updateProduct(long productId, ProductRequest productRequest) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        if(productRequest.getName() != null) {
            product.setName(productRequest.getName());
        }

        if(productRequest.getDescription() != null) {
            product.setDescription(productRequest.getDescription());
        }

        if(productRequest.getPrice() > 0) {
            product.setPrice(productRequest.getPrice());
        }

        if(productRequest.getQuantity() > 0) {
            product.setQuantity(productRequest.getQuantity());
        }

        return productRepository.save(product);
    }
}