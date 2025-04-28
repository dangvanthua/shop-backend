package com.thuan.shop_backend.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thuan.shop_backend.component.AuthComponent;
import com.thuan.shop_backend.constant.OrderStatus;
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
import com.thuan.shop_backend.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final IProductRedisService productRedisService;
    private final IUserService userService;
    private final AuthComponent authComponent;

    @Override
    @Transactional
    @PreAuthorize("hasRole('SELLER') OR hasRole('ADMIN')")
    public Product createProduct(ProductRequest productRequest) {

        Seller seller = sellerRepository.findByUserId(productRequest.getSellerId())
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
    @PreAuthorize("hasRole('SELLER') OR hasRole('ADMIN')")
    public void uploadProductImages(
            long productId,
            Map<String, String> productImageUrl,
            boolean isThumbnail) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        long existingThumbnailCount = productImageRepository
                .countByProductIdAndImage(productId, true);

        if (isThumbnail && existingThumbnailCount > 0) {
            List<ProductImage> existingThumbnails = productImageRepository
                    .findByProductIdAndImage(productId, true);

            if(!existingThumbnails.isEmpty()) {
                ProductImage existingThumbnail = existingThumbnails.getFirst();

                if (existingThumbnail.getCloudinaryPublicId() != null) {
                    fileService.deleteFile(existingThumbnail.getCloudinaryPublicId());
                }

                productImageRepository.delete(existingThumbnail);
            }
        }else {
            long galleryCount = productImageRepository
                    .countByProductIdAndImage(productId, false);

            int maxGalleries = 5;

            if(galleryCount + productImageUrl.size() > maxGalleries) {
                List<ProductImage> galleryImages = productImageRepository
                        .findByProductIdAndImage(productId, false);

                int excessCount = (int) ((galleryCount + productImageUrl.size()) - maxGalleries);

                for(int i = 0; i < excessCount; i++) {
                    ProductImage oldProductImage = galleryImages.get(i);

                    if(oldProductImage.getCloudinaryPublicId() != null) {
                        fileService.deleteFile(oldProductImage.getCloudinaryPublicId());
                    }

                    productImageRepository.delete(oldProductImage);
                }
            }
        }

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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        List<ProductImageResponse> imageResponses = Optional.ofNullable(productImageRepository.findByProductId(product.getId()))
                .orElse(Collections.emptyList())
                .stream()
                .map(image -> ProductImageResponse.builder()
                        .imageUrl(image.getImageUrl())
                        .isThumbnail(image.getIsThumbnail())
                        .build())
                .collect(Collectors.toList());


        Seller seller = sellerRepository.findSellerByProductId(product.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SELLER_NOT_EXISTED));

        long totalProductsSold = sellerRepository.countProductsSoldBySeller(seller.getId());
        long totalReviews = reviewRepository.countReviewsBySellerId(seller.getId());

        SellerInfoResponse sellerInfo = SellerInfoResponse.fromSeller(
                seller, totalProductsSold, totalReviews);

        List<ProductPromotionCode> productPromotionCode = promotionCodeRepository
                .findByProductId(product.getId(), LocalDate.now());

        List<PromotionCodeResponse> promotionCodeResponses = productPromotionCode.stream()
                .map(PromotionCodeResponse::fromProductPromotion)
                .toList();

        return ProductDetailResponse.fromProductDetail(
                product,
                imageResponses,
                promotionCodeResponses,
                sellerInfo);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') OR hasRole('SELLER')")
    public Product updateProduct(long productId, ProductRequest productRequest) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        boolean isAdmin = authComponent.checkIsAdmin();

        if(!isAdmin) {
            String emailUser = authComponent.getEmailFromAuthentication();
            User user = userService.getUserByEmail(emailUser);

            Seller seller = sellerRepository.findSellerByProductId(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.SELLER_NOT_EXISTED));

            if(!seller.getUser().getId().equals(user.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

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

    @Override
    @Transactional
    public Product updateProductQuantity(long productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        if(productRequest.getQuantity() > 0) {
            product.setQuantity(productRequest.getQuantity());
        }
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponse> getProductByKeyWord(String keyword) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String redisKey = productRedisService.getCacheKey(keyword);
        String lockKey = "lock:" + redisKey;
        String lockValue = "locked";
        int lockExpireTime = 10;

        Optional<String> cachedResult = productRedisService.getFromCache(redisKey);

        if (cachedResult.isPresent()) {
            try {
                return objectMapper.readValue(cachedResult.get(), new TypeReference<List<ProductResponse>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse cached result", e);
            }
        }

        if (productRedisService.acquireLock(lockKey, lockValue, lockExpireTime)) {
            try {
                Pageable pageable = PageRequest.of(0, 12);
                List<Product> products = productRepository.findProductByKeyword(keyword, pageable);

                // get images
                List<Long> productIds = products.stream()
                        .map(Product::getId)
                        .toList();
                List<ProductImage> images = productImageRepository.findByProductIds(productIds);

                Map<Long, List<ProductImage>> imagesGroupedByProduct = images.stream()
                        .collect(Collectors.groupingBy(image -> image.getProduct().getId()));

                List<ProductResponse> productResponses = products.stream()
                        .map((product) -> {
                            List<ProductImage> productImages = imagesGroupedByProduct
                                    .getOrDefault(product.getId(), Collections.emptyList());
                            return ProductResponse.fromProduct(product, productImages);
                        })
                        .collect(Collectors.toList());

                String jsonValue = objectMapper.writeValueAsString(productResponses);
                productRedisService.saveToCache(redisKey, jsonValue);
                return productResponses;
            } catch (Exception e) {
                throw new RuntimeException("Failed to populate cache", e);
            } finally {
                productRedisService.releaseLock(lockKey, lockValue);
            }
        } else {
            try {
                Thread.sleep(500);
                return getProductByKeyWord(keyword);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while waiting for lock", e);
            }
        }
    }
}