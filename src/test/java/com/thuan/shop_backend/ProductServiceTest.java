package com.thuan.shop_backend;

import com.thuan.shop_backend.dto.request.product.ProductRequest;
import com.thuan.shop_backend.entity.Category;
import com.thuan.shop_backend.entity.Product;
import com.thuan.shop_backend.entity.Seller;
import com.thuan.shop_backend.repository.CategoryRepository;
import com.thuan.shop_backend.repository.ProductRepository;
import com.thuan.shop_backend.repository.SellerRepository;
import com.thuan.shop_backend.service.product.ProductService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testCreateProductSuccess() {

        // Given: prepare data for testing
        ProductRequest productRequest = ProductRequest.builder()
                .name("Test product name")
                .description("Test description")
                .price(100000)
                .quantity(10)
                .sellerId(9)
                .categoryId(11)
                .build();

        Seller seller = Seller.builder()
                .id(9L)
                .storeName("LADOS")
                .build();

        Category category = Category.builder()
                .id(11L)
                .name("Thời trang nữ")
                .build();

        when(sellerRepository.findByUserId(productRequest.getSellerId()))
                .thenReturn(Optional.of(seller));

        when(categoryRepository.findById(productRequest.getCategoryId()))
                .thenReturn(Optional.of(category));

        Product savedProduct = Product.builder()
                .id(123L)
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .seller(seller)
                .category(category)
                .isActive(false)
                .build();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // when: call method need test
        Product result = productService.createProduct(productRequest);

        // then: check result
        assertNotNull(result);
        assertEquals(123L, result.getId());
        assertEquals("Test product name", result.getName());
        assertEquals("Test description", result.getDescription());
        assertEquals(100000, result.getPrice());
        assertEquals(10, result.getQuantity());
        assertFalse(result.getIsActive());
        assertEquals(seller, result.getSeller());
        assertEquals(category, result.getCategory());

        verify(sellerRepository, times(1)).findByUserId(productRequest.getSellerId());
        verify(categoryRepository, times(1)).findById(productRequest.getCategoryId());
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
