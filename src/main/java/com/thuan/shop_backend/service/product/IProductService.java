package com.thuan.shop_backend.service.product;

import com.thuan.shop_backend.dto.request.ProductRequest;
import com.thuan.shop_backend.dto.request.ProductVariantRequest;
import com.thuan.shop_backend.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    void createVariant(long productId, List<ProductVariantRequest> productVariantRequests);
    void uploadProductImages(long productId, Map<String, String> productImageUrl, boolean isThumbnail);
    Page<ProductResponse> getProductByCategory(long categoryId, Pageable pageable);
    List<ProductResponse> recommendProducts(long productId, int topN);
    Page<ProductResponse> getFeatureProducts(int page, int size);
}
