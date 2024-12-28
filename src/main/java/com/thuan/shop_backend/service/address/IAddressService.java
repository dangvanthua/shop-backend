package com.thuan.shop_backend.service.address;

import com.thuan.shop_backend.dto.request.address.AddressRequest;
import com.thuan.shop_backend.dto.response.address.AddressResponse;
import com.thuan.shop_backend.entity.Address;

import java.util.List;

public interface IAddressService {
    void createAddress(AddressRequest addressRequest);
    void updateAddress(long addressId, AddressRequest addressRequest);
    List<AddressResponse> getAllAddress();
}
