package com.thuan.shop_backend.service.product;

import com.thuan.shop_backend.dto.response.ProductResponse;
import com.thuan.shop_backend.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    @Override
    public List<ProductResponse> getProductByCategory(long categoryId) {
        return List.of();
    }
}
