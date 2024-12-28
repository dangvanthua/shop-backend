package com.thuan.shop_backend.service.cart;

import com.thuan.shop_backend.component.AuthComponent;
import com.thuan.shop_backend.dto.request.cart.CartRequest;
import com.thuan.shop_backend.dto.response.cart.CartResponse;
import com.thuan.shop_backend.dto.response.product.ProductResponse;
import com.thuan.shop_backend.entity.CartItem;
import com.thuan.shop_backend.entity.Product;
import com.thuan.shop_backend.entity.ProductImage;
import com.thuan.shop_backend.entity.User;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.CartRepository;
import com.thuan.shop_backend.repository.ProductImageRepository;
import com.thuan.shop_backend.repository.ProductRepository;
import com.thuan.shop_backend.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService{

    private final CartRepository cartRepository;
    private final AuthComponent authComponent;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ModelMapper mapper;

    private final IUserService userService;

    @Override
    public CartItem createCartItem(CartRequest cartRequest) {

        CartItem cartItem = mapper.map(cartRequest, CartItem.class);

        // get info email of user from security context
        String emailUser = authComponent.getEmailFromAuthentication();

        User user = userService.getUserByEmail(emailUser);

        Product product = productRepository.findById(cartRequest.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        if(cartRequest.getQuantity() > product.getQuantity()) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }

        cartItem.setUser(user);
        cartItem.setProduct(product);

        return cartRepository.save(cartItem);
    }

    @Override
    public CartItem updateCartItem(long cartId, CartRequest cartRequest) {

        if(cartRequest.getQuantity() <= 0) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }

        String emailUser = authComponent.getEmailFromAuthentication();

        CartItem cartItem = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        if(!cartItem.getUser().getEmail().equals(emailUser)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Product product = cartItem.getProduct();
        if(product == null || (product.getId() != cartRequest.getProductId())) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }

        if(cartRequest.getQuantity() <= 0 || cartRequest.getQuantity() > 10) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }

        // Update quantity of product in cart
        cartItem.setQuantity(cartRequest.getQuantity());

        return cartRepository.save(cartItem);
    }

    @Override
    public void removeCartItems(List<Long> productIds) {

        String email = authComponent.getEmailFromAuthentication();

        productIds.forEach(productId -> {
            CartItem cartItem = cartRepository.findByProductIdAndEmailUser(productId, email)
                    .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

            if(!cartItem.getUser().getEmail().equals(email)) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            cartRepository.delete(cartItem);
        });
    }

    @Override
    public void removeCartItem(long productId) {

        String emailUser = authComponent.getEmailFromAuthentication();

        CartItem cartItem = cartRepository
                .findByProductIdAndEmailUser(productId, emailUser)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        cartRepository.delete(cartItem);
    }
    
    @Override
    public List<CartResponse> getAllCartItems() {

        // get info of user
        String email = authComponent.getEmailFromAuthentication();
        User user = userService.getUserByEmail(email);

        List<CartItem> cartItems = cartRepository.findByUserId(user.getId());

        List<CartResponse> cartResponses = new ArrayList<>();
        if(cartItems != null) {
             cartResponses = cartItems.stream()
                     .map(cartItem -> {

                         List<ProductImage> productImages = productImageRepository
                                 .findByProductId(cartItem.getProduct().getId());

                         return CartResponse.builder()
                                 .id(cartItem.getId())
                                 .quantity(cartItem.getQuantity())
                                 .addedAt(cartItem.getAddedAt())
                                 .productResponse(ProductResponse
                                         .fromProduct(cartItem.getProduct(), productImages))
                                 .build();
                     })
                     .toList();
        }

        return cartResponses;
    }

    @Override
    public List<CartResponse> getCartItemsByIds(List<Long> productIds) {

        String email = authComponent.getEmailFromAuthentication();
        User user = userService.getUserByEmail(email);

        List<CartItem> cartItems = cartRepository.findByUserIdAndProductIds(
                user.getId(), productIds);

        List<CartResponse> cartResponses = new ArrayList<>();
        if(cartItems != null) {
            cartResponses = cartItems.stream()
                    .map(cartItem -> {

                        List<ProductImage> productImages = productImageRepository
                                .findByProductId(cartItem.getProduct().getId());

                        return CartResponse.builder()
                                .id(cartItem.getId())
                                .quantity(cartItem.getQuantity())
                                .addedAt(cartItem.getAddedAt())
                                .productResponse(ProductResponse
                                        .fromProduct(cartItem.getProduct(), productImages))
                                .build();
                    })
                    .toList();
        }

        return cartResponses;
    }
}
