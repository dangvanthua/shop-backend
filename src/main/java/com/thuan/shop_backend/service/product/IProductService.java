package com.thuan.shop_backend.service.product;

import com.thuan.shop_backend.dto.request.ProductRequest;
import com.thuan.shop_backend.dto.request.ProductVariantRequest;
import com.thuan.shop_backend.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    void createVariant(long productId, List<ProductVariantRequest> productVariantRequests);
    void uploadProductImages(long productId, Map<String, String> productImageUrl, boolean isThumbnail);
    List<ProductResponse> getProductByCategory(long categoryId);
}
