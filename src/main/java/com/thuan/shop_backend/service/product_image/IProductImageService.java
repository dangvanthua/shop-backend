package com.thuan.shop_backend.service.product_image;

import com.thuan.shop_backend.entity.ProductImage;

import java.util.List;

public interface IProductImageService {
    List<ProductImage> getByProductIds(List<Long> productIds);
}
