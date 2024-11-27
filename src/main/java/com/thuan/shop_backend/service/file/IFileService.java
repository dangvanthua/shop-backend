package com.thuan.shop_backend.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface IFileService {
    Map<String, Object> uploadFile(MultipartFile file, String folderName);
    void deleteFile(String publicId);
}
