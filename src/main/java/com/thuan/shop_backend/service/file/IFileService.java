package com.thuan.shop_backend.service.file;

import com.thuan.shop_backend.dto.request.order.OrderPdfRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IFileService {
    Map<String, Object> uploadFile(MultipartFile file, String folderName);
    void deleteFile(String publicId);
    byte[] exportOrderPdf(OrderPdfRequest orderPdfRequest) throws IOException;
}
