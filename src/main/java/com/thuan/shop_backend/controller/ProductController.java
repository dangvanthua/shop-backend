package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.component.FilePathComponent;
import com.thuan.shop_backend.dto.request.product.ProductRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.product.ProductDetailResponse;
import com.thuan.shop_backend.dto.response.product.ProductListResponse;
import com.thuan.shop_backend.dto.response.product.ProductResponse;
import com.thuan.shop_backend.entity.Product;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.service.file.IFileService;
import com.thuan.shop_backend.service.product.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;
    private final FilePathComponent filePathComponent;
    private final IFileService fileService;

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest) {
        Product product = productService.createProduct(productRequest);
        ProductResponse productResponse = ProductResponse.fromProduct(product, null);
        return ApiResponse.<ProductResponse>builder()
                .message("Create product success")
                .result(productResponse)
                .build();
    }

    @PostMapping(
            value = "/{id}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> uploadProductImages(
            @PathVariable("id") long productId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("thumbnail") boolean isThumbnail) {

        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }

        if(files.size() > 5 || (isThumbnail && files.size() > 1)) {
            throw new AppException(ErrorCode.LIMIT_FILE);
        }

        String folderName = null;
        Map<String, String> productImages = new HashMap<>();

        if(isThumbnail) {
            folderName = filePathComponent.getProductThumbnailPath();
        } else {
            folderName = filePathComponent.getProductGalleryPath();
        }

        for (MultipartFile file : files) {
            Map resultUpload = fileService.uploadFile(file, folderName);
            String publicId = (String) resultUpload.get("public_id");
            String imageUrl = (String) resultUpload.get("url");
            productImages.put(publicId, imageUrl);
        }

        productService.uploadProductImages(productId, productImages, isThumbnail);

        return ApiResponse.<Void>builder()
                .message(isThumbnail ?
                        "Upload thumbnail image success"
                        : "Upload gallery images success")
                .build();
    }

    @GetMapping("/{id}/category")
    public ApiResponse<ProductListResponse> getProductByCategory(
            @PathVariable("id") long categoryId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ProductResponse> productResponses = productService.getProductByCategory(categoryId, pageable);

        ProductListResponse response = ProductListResponse.builder()
                .productResponses(productResponses.getContent())
                .totalPages(productResponses.getTotalPages())
                .totalItems(productResponses.getTotalElements())
                .build();

        return ApiResponse.<ProductListResponse>builder()
                .message("Get product by category success")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<ProductListResponse> getFeatureProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Page<ProductResponse> productResponses = productService.getFeatureProducts(page, size);

        ProductListResponse response = ProductListResponse.builder()
                .productResponses(productResponses.getContent())
                .totalPages(productResponses.getTotalPages())
                .totalItems(productResponses.getTotalElements())
                .build();

        return ApiResponse.<ProductListResponse>builder()
                .message("Get feature products success")
                .result(response)
                .build();
    }

    @GetMapping("/{id}/detail")
    public ApiResponse<ProductDetailResponse> getProductDetail(
            @PathVariable("id") long productId) {
        ProductDetailResponse productDetailResponse = productService.getProductDetail(productId);
        return ApiResponse.<ProductDetailResponse>builder()
                .message("Get product detail success")
                .result(productDetailResponse)
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<ProductResponse>> searchProducts(
            @RequestParam("q") String keyword) {

        if(keyword == null || keyword.trim().isEmpty()) {
            return ApiResponse.<List<ProductResponse>>builder()
                    .message("Keyword is empty. No products found.")
                    .result(Collections.emptyList())
                    .build();
        }

        List<ProductResponse> productResponses = productService.getProductByKeyWord(keyword);

        return ApiResponse.<List<ProductResponse>>builder()
                .message("Get products with keyword '" + keyword + "' success")
                .result(productResponses)
                .build();
    }
}
