package com.thuan.shop_backend.service.product;

import com.thuan.shop_backend.dto.request.product.ProductRequest;
import com.thuan.shop_backend.dto.response.product.ProductDetailResponse;
import com.thuan.shop_backend.dto.response.product.ProductResponse;
import com.thuan.shop_backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IProductService {
    Product createProduct(ProductRequest productRequest);
    void uploadProductImages(long productId, Map<String, String> productImageUrl, boolean isThumbnail);
    Page<ProductResponse> getProductByCategory(long categoryId, Pageable pageable);
    Page<ProductResponse> getFeatureProducts(int page, int size);
    ProductDetailResponse getProductDetail(long productId);
    Product updateProduct(long productId, ProductRequest productRequest);
}
