package com.thuan.shop_backend.service.address;

import com.thuan.shop_backend.component.AuthComponent;
import com.thuan.shop_backend.dto.request.address.AddressRequest;
import com.thuan.shop_backend.dto.response.address.AddressResponse;
import com.thuan.shop_backend.entity.Address;
import com.thuan.shop_backend.entity.User;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.AddressRepository;
import com.thuan.shop_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService{

    private final AddressRepository addressRepository;
    private final UserService userService;
    private final AuthComponent authComponent;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public void createAddress(AddressRequest addressRequest) {
        List<Address> addressList = addressRepository.findAll();
        String email = authComponent.getEmailFromAuthentication();
        User user = userService.getUserByEmail(email);

        Address address = mapper.map(addressRequest, Address.class);
        address.setUser(user);
        address.setIsDefault(addressList.isEmpty());

        addressRepository.save(address);
    }

    @Override
    @Transactional
    public void updateAddress(
            long addressId,
            AddressRequest addressRequest) {

        String email = authComponent.getEmailFromAuthentication();
        User user = userService.getUserByEmail(email);

        Address address = addressRepository.findByUserIdAndAddressId(user.getId(), addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXISTED));

        if(addressRequest.getAddressLine() != null) {
            address.setAddressLine(addressRequest.getAddressLine());
        }

        if(addressRequest.getWard() != null) {
            address.setWard(addressRequest.getWard());
        }

        if(addressRequest.getDistrict() != null) {
            address.setDistrict(addressRequest.getDistrict());
        }

        if(addressRequest.getCity() != null) {
            address.setCity(addressRequest.getCity());
        }

        if(addressRequest.getCountry() != null) {
            address.setCountry(addressRequest.getCountry());
        }

        addressRepository.save(address);
    }

    @Override
    public List<AddressResponse> getAllAddress() {
        String email = authComponent.getEmailFromAuthentication();
        User user = userService.getUserByEmail(email);
        List<Address> addressList = addressRepository.findByUserId(user.getId());
        return addressList.stream()
                .map(AddressResponse::fromAddress)
                .toList();
    }
}
