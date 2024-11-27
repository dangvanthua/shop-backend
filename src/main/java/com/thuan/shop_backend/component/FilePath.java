package com.thuan.shop_backend.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class FilePath {
    @Value("${file.upload.user.avatar}")
    private String userAvatarPath;

    @Value("${file.upload.product.thumbnail}")
    private String productThumbnailPath;

    @Value("${file.upload.product.gallery}")
    private String productGalleryPath;

    @Value("${file.upload.review.image}")
    private String reviewImagePath;

    @Value("${file.upload.review.video}")
    private String reviewVideoPath;
}
