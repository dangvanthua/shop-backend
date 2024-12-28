package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.address.AddressRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.address.AddressResponse;
import com.thuan.shop_backend.service.address.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final IAddressService addressService;

    @PostMapping
    public ApiResponse<Void> createAddress(@RequestBody AddressRequest addressRequest) {
        addressService.createAddress(addressRequest);
        return ApiResponse.<Void>builder()
                .message("Create address success")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateAddress(
            @PathVariable("id") long addressId,
            @RequestBody AddressRequest addressRequest) {
        addressService.updateAddress(addressId, addressRequest);
        return ApiResponse.<Void>builder()
                .message("Update address success")
                .build();
    }

    @GetMapping
    public ApiResponse<List<AddressResponse>> getAllAddress() {
        List<AddressResponse> addressResponses = addressService.getAllAddress();
        return ApiResponse.<List<AddressResponse>>builder()
                .message("Get all address success")
                .result(addressResponses)
                .build();
    }
}
