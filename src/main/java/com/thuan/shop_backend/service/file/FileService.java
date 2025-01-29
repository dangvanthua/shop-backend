package com.thuan.shop_backend.service.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.thuan.shop_backend.constant.PaymentMethod;
import com.thuan.shop_backend.dto.request.order.OrderPdfRequest;
import com.thuan.shop_backend.entity.Order;
import com.thuan.shop_backend.entity.OrderDetail;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.OrderDetailRepository;
import com.thuan.shop_backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService implements IFileService{

    private final Cloudinary cloudinary;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public Map uploadFile(MultipartFile file, String folderName) {
        try {

            if (file.getSize() > 15 * 1024 * 1024) {
                throw new AppException(ErrorCode.FILE_TOO_LARGE);
            }

            return cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", folderName));
        } catch (IOException e) {
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new AppException(ErrorCode.DELETE_FILE_FAILED);
        }
    }

    @Override
    public byte[] exportOrderPdf(OrderPdfRequest orderPdfRequest) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("HÓA ĐƠN MUA HÀNG", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            Order order = orderPdfRequest.getOrder();

            String paymentMethod = "";
            if(order.getPaymentMethod().equalsIgnoreCase(PaymentMethod.E_WALLET.name())) {
                paymentMethod = "Ví điện tử";
            }else if(order.getPaymentMethod().equalsIgnoreCase(PaymentMethod.CREDIT_CARD.name())) {
                paymentMethod = "Ngân hàng";
            }else {
                paymentMethod = "Thanh toán khi nhận hàng";
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

            String orderDateFormatted = order.getOrderDate()
                    .atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh"))
                    .format(formatter);

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            document.add(new Paragraph("Mã đơn hàng: " + order.getId()));
            document.add(new Paragraph("Ngày đặt hàng: " + orderDateFormatted));
            document.add(new Paragraph("Địa chỉ giao hàng: " + order.getShippingAddress()));
            document.add(new Paragraph("Phương thức thanh toán: " + paymentMethod));

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Sản phẩm");
            table.addCell("Số lượng");
            table.addCell("Đơn giá");
            table.addCell("Tổng tiền");

            for (OrderDetail detail : orderPdfRequest.getOrderDetail()) {
                String formattedPrice = currencyFormat.format(detail.getPrice());
                String formattedTotalPrice = currencyFormat.format(detail.getTotalMoney());
                table.addCell(detail.getProduct().getName());
                table.addCell(detail.getNumberOfProducts().toString());
                table.addCell(formattedPrice);
                table.addCell(formattedTotalPrice);
            }

            document.add(table);

            document.add(new Paragraph(" "));

            String formattedTotalPrice = currencyFormat.format(orderPdfRequest.getOrderDetail().stream()
                    .mapToDouble(OrderDetail::getTotalMoney).sum());
            document.add(new Paragraph("Tổng tiền: " + formattedTotalPrice));

            document.close();

            return outputStream.toByteArray();
    }
}
