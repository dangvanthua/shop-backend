package com.thuan.shop_backend.service.product_image;

import com.thuan.shop_backend.entity.ProductImage;
import com.thuan.shop_backend.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageService implements IProductImageService{

    private final ProductImageRepository productImageRepository;


    @Override
    public List<ProductImage> getByProductIds(List<Long> productIds) {

        List<ProductImage> productImages = productImageRepository
                .findByProductIds(productIds);

        if(productImages.isEmpty()) {
            return null;
        }

        return productImages;
    }
}
