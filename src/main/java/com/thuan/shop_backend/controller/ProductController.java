package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.component.FilePathComponent;
import com.thuan.shop_backend.dto.request.ProductRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.ProductResponse;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.service.file.IFileService;
import com.thuan.shop_backend.service.product.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        ProductResponse productResponse = productService.createProduct(productRequest);
        return ApiResponse.<ProductResponse>builder()
                .message("Create product success")
                .result(productResponse)
                .build();
    }

    @PostMapping(
            value = "/images/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> uploadProductImages(
            @PathVariable("id") long productId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("thumbnail") boolean isThumbnail) {

        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
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
}
