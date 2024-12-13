package com.thuan.shop_backend.service.seller;

import com.thuan.shop_backend.dto.request.seller.SellerRequest;
import com.thuan.shop_backend.dto.response.seller.SellerResponse;
import com.thuan.shop_backend.entity.Seller;

import java.util.List;

public interface ISellerService {
    Seller createSeller(SellerRequest sellerRequest);
    List<SellerResponse> getAllSellers();
    Seller updateSeller(SellerRequest sellerRequest);
    SellerResponse getSeller(long sellerId);
}
