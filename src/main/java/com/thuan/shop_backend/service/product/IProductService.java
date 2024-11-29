package com.thuan.shop_backend.service.product;

import com.thuan.shop_backend.dto.response.ProductResponse;

import java.util.List;

public interface IProductService {
    List<ProductResponse> getProductByCategory(long categoryId);
}
