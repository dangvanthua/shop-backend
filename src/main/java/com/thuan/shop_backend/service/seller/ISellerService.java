package com.thuan.shop_backend.service.seller;

import com.thuan.shop_backend.dto.request.SellerRequest;
import com.thuan.shop_backend.dto.response.SellerResponse;

import java.util.List;

public interface ISellerService {
    SellerResponse createSeller(SellerRequest sellerRequest);
    List<SellerResponse> getAllSellers();
    SellerResponse updateSeller(SellerRequest sellerRequest);
    SellerResponse getSeller(long sellerId);
}
