package com.nguyenthithuhuyen.example10.security.services;

import com.nguyenthithuhuyen.example10.entity.*;
import com.nguyenthithuhuyen.example10.entity.enums.OrderStatus;
import com.nguyenthithuhuyen.example10.entity.enums.PaymentStatus;
import com.nguyenthithuhuyen.example10.entity.enums.Status;
import com.nguyenthithuhuyen.example10.payload.request.BillRequest;
import com.nguyenthithuhuyen.example10.repository.BillRepository;
import com.nguyenthithuhuyen.example10.repository.OrderRepository;
import com.nguyenthithuhuyen.example10.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import com.itextpdf.text.Element;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;

    // Các hàm dịch vụ chính
    // --------------------------------------------------------------------------------

    @Transactional
    public Bill create(BillRequest request) {
        if (request.getOrderId() == null) {
            throw new IllegalArgumentException("Lỗi: Order ID không được để trống khi tạo hóa đơn.");
        }
        
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đơn hàng #" + request.getOrderId()));

        // 1. NGĂN CHẶN LỖI TRÙNG LẶP (Duplicate Entry)
        if (billRepository.existsByOrderId(order.getId())) {
            throw new RuntimeException("Lỗi: Hóa đơn đã tồn tại cho đơn hàng #" + order.getId());
        }

        // 💡 CẬP NHẬT TRẠNG THÁI ORDER SANG COMPLETED (Đã thanh toán)
        // Vì Bill chỉ được tạo khi thanh toán, ta cập nhật trạng thái của Order
        order.setStatus(OrderStatus.PAID); 
        orderRepository.save(order);
        
        Bill bill = Bill.builder()
                .order(order)
                .table(order.getTable())
                // LẤY TỔNG TIỀN CUỐI CÙNG TỪ ORDER KHI TẠO
                .totalAmount(order.getFinalAmount()) 
                .paymentMethod(request.getPaymentMethod())
                // MẶC ĐỊNH TRẠNG THÁI LÀ PENDING (hoặc COMPLETED nếu thanh toán ngay)
                .paymentStatus(PaymentStatus.COMPLETED) // Giả định khi tạo bill là đã thanh toán
                .note(request.getNote())
                .issuedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // GIẢI PHÓNG BÀN NGAY LẬP TỨC
        TableEntity table = order.getTable();
        if (table != null && table.getStatus() != Status.FREE) {
            table.setStatus(Status.FREE);
            tableRepository.save(table);
        }

        return billRepository.save(bill);
    }

    public Bill getById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy hóa đơn #" + id));
    }

    public List<Bill> getAll() {
        return billRepository.findAll();
    }

    @Transactional
    public Bill update(Long id, Bill billUpdateData) {
        Bill existing = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy hóa đơn #" + id));

        // Lưu lại trạng thái cũ trước khi update
        PaymentStatus oldStatus = existing.getPaymentStatus();
        
        // CẬP NHẬT DỮ LIỆU CÓ ĐIỀU KIỆN
        if (billUpdateData.getPaymentStatus() != null) {
            existing.setPaymentStatus(billUpdateData.getPaymentStatus());
        }
        if (billUpdateData.getPaymentMethod() != null) {
            existing.setPaymentMethod(billUpdateData.getPaymentMethod());
        }
        if (billUpdateData.getNote() != null) {
            existing.setNote(billUpdateData.getNote());
        }

        // Cập nhật TotalAmount nếu có giá trị mới gửi lên (cho phép chỉnh sửa cuối cùng)
        if (billUpdateData.getTotalAmount() != null) {
            existing.setTotalAmount(billUpdateData.getTotalAmount());
        }
        
        existing.setUpdatedAt(LocalDateTime.now());
        
        Bill updatedBill = billRepository.save(existing);
        
        // LOGIC XỬ LÝ KHI CHUYỂN TRẠNG THÁI THANH TOÁN
        if (oldStatus != PaymentStatus.COMPLETED && updatedBill.getPaymentStatus() == PaymentStatus.COMPLETED) {
            Order order = updatedBill.getOrder();
            if (order != null) {
                // Đảm bảo Order cũng được đánh dấu là PAID
                order.setStatus(OrderStatus.PAID);
                orderRepository.save(order);

                // Cập nhật trạng thái bàn sang FREE
                TableEntity table = order.getTable();
                if (table != null && table.getStatus() != Status.FREE) {
                    table.setStatus(Status.FREE);
                    tableRepository.save(table);
                }
            }
        }

        return updatedBill;
    }

    public void delete(Long id) {
        billRepository.deleteById(id);
    }

    // CÁC HÀM XỬ LÝ PDF (Đã sửa lỗi chữ ký hàm)
    // --------------------------------------------------------------------------------
    
    /**
     * 🟢 PHƯƠNG THỨC ĐÃ SỬA LỖI BIÊN DỊCH: Nhận Long billId và trả về byte[]
     */
    public byte[] exportToPdfBytes(Long billId) {
        Bill bill = getById(billId); // Tự tìm Bill
        return generatePdfBytes(bill); 
    }
    
    // Đổi tên hàm cũ exportToPdfBytes(Bill) thành generatePdfBytes(Bill) để rõ ràng hơn
    private byte[] generatePdfBytes(Bill bill) {
        // 4. KIỂM TRA TRẠNG THÁI TRƯỚC KHI XUẤT
        checkCanExport(bill); 

        Document document = new Document();
        try {
            Font font = getVietnameseFont();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            addBillContentToDocument(document, bill, font);

            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("❌ Lỗi khi tạo PDF: " + e.getMessage(), e);
        }
    }

    // PHƯƠNG THỨC GỐC XUẤT RA FILE (Giữ nguyên)
    public void exportToPdf(Bill bill, String filePath) {
        checkCanExport(bill); 

        Document document = new Document();
        try {
            Font font = getVietnameseFont();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            addBillContentToDocument(document, bill, font);

            document.close();
            System.out.println("✅ PDF đã được tạo tại: " + filePath);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("❌ Lỗi khi tạo PDF: " + e.getMessage(), e);
        }
    }
    
    private void checkCanExport(Bill bill) {
        if (bill.getPaymentStatus() != PaymentStatus.COMPLETED) {
            String message = String.format(
                "Lỗi Xuất PDF: Không thể xuất hóa đơn #%d vì trạng thái thanh toán hiện tại là: %s. Hóa đơn phải là COMPLETED mới có thể xuất.", 
                bill.getId(), 
                bill.getPaymentStatus().name()
            );
            throw new RuntimeException(message);
        }
    }

    private Font getVietnameseFont() throws DocumentException, IOException {
        // Thử load font từ classpath (/fonts/) trước
        String[] classpathCandidates = new String[]{
                "/fonts/NotoSans-Regular.ttf",
                "/fonts/NotoSansVietnamese-Regular.ttf",
                "/fonts/DejaVuSans.ttf",
                "/fonts/arial.ttf"
        };
        
        for (String path : classpathCandidates) {
            try (InputStream is = this.getClass().getResourceAsStream(path)) {
                if (is == null) continue;
                byte[] fontBytes = is.readAllBytes();
                BaseFont bf = BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);
                System.out.println("[BillService] ✓ Using classpath font: " + path);
                return new Font(bf, 12);
            } catch (Exception ex) {
                System.out.println("[BillService] ✗ Classpath font failed: " + path + " -> " + ex.getMessage());
            }
        }

            // Ưu tiên tìm font trên hệ thống Windows (thường đầy đủ glyph Unicode)
            String[] systemCandidates = new String[]{
                    "C:\\Windows\\Fonts\\NotoSans-Regular.ttf",
                    "C:\\Windows\\Fonts\\NotoSansVietnamese-Regular.ttf",
                    "C:\\Windows\\Fonts\\arial.ttf",
                    "C:\\Windows\\Fonts\\DejaVuSans.ttf",
                    "C:\\Windows\\Fonts\\Tahoma.ttf"
            };

        for (String sysPath : systemCandidates) {
            try {
                File f = new File(sysPath);
                if (!f.exists()) continue;
                BaseFont bf = BaseFont.createFont(f.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                System.out.println("[BillService] ✓ Using system font: " + f.getAbsolutePath());
                return new Font(bf, 12);
            } catch (Exception ex) {
                System.out.println("[BillService] ✗ System font failed: " + sysPath + " -> " + ex.getMessage());
            }
        }

        // Fallback cuối cùng: Helvetica (có thể không hiển thị dấu tiếng Việt đúng)
        System.out.println("[BillService] ⚠ WARNING: Falling back to Helvetica (Vietnamese text may not display correctly)");
        BaseFont bfFallback = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        return new Font(bfFallback, 12);
    }

    private void addBillContentToDocument(Document document, Bill bill, Font font) throws DocumentException {
    // Tiêu đề hóa đơn (dùng BaseFont để giữ Unicode)
    Font titleFont = new Font(font.getBaseFont(), 18, Font.BOLD);
    Paragraph title = new Paragraph("🧾 HÓA ĐƠN THANH TOÁN", titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);
    document.add(new Paragraph("\n")); // thêm khoảng trắng

    // Định dạng số dùng chung cho toàn bộ hàm
    DecimalFormat df = new DecimalFormat("#,##0.00");

    // Thông tin cơ bản (dùng BaseFont để giữ Unicode)
    Font boldFont = new Font(font.getBaseFont(), 12, Font.BOLD);
    document.add(new Paragraph("Mã hóa đơn: #" + bill.getId(), font));
    document.add(new Paragraph("Ngày xuất: " + bill.getIssuedAt(), font));
    document.add(new Paragraph("Tên khách hàng: " +
            (bill.getOrder() != null && bill.getOrder().getUser() != null
                    ? bill.getOrder().getUser().getFullName()
                    : "Không xác định"), font));
    document.add(new Paragraph("Bàn: " +
            (bill.getTable() != null ? bill.getTable().getTableNumber() : "Không xác định"), font));
    document.add(new Paragraph("\n")); // khoảng trắng

    // Danh sách sản phẩm bằng bảng
    document.add(new Paragraph("📦 Danh sách sản phẩm:", boldFont));

    if (bill.getOrder() != null && bill.getOrder().getOrderItems() != null && !bill.getOrder().getOrderItems().isEmpty()) {
        PdfPTable table = new PdfPTable(new float[]{4, 1, 2, 2}); // tên | SL | giá | thành tiền
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Tiêu đề cột
        String[] headers = {"Sản phẩm", "SL", "Đơn giá (VND)", "Thành tiền (VND)"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Nội dung sản phẩm
        for (OrderItem item : bill.getOrder().getOrderItems()) {
            if (item.getProduct() != null) {
                table.addCell(new PdfPCell(new Phrase(item.getProduct().getName(), font)));
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), font));
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qtyCell);
                PdfPCell priceCell = new PdfPCell(new Phrase(df.format(item.getPrice()), font));
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(priceCell);
                PdfPCell subtotalCell = new PdfPCell(new Phrase(df.format(item.getSubtotal()), font));
                subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(subtotalCell);
            }
        }
        document.add(table);
    }

    // Khuyến mãi
    Promotion promo = bill.getOrder() != null ? bill.getOrder().getPromotion() : null;
    if (promo != null) {
        document.add(new Paragraph("🎁 Khuyến mãi áp dụng: " + promo.getName(), font));
        if (promo.getDiscountPercent() != null) {
            document.add(new Paragraph("Giảm: " + promo.getDiscountPercent() + "%", font));
        } else if (promo.getDiscountAmount() != null) {
            document.add(new Paragraph("Giảm: " + df.format(promo.getDiscountAmount()) + " VND", font));
        }
    }

    document.add(new Paragraph("\n")); // khoảng trắng

    // Tổng tiền
    document.add(new Paragraph("Tổng tiền trước giảm: " + 
            (bill.getOrder() != null ? df.format(bill.getOrder().getTotalAmount()) : "N/A") + " VND", boldFont));
    document.add(new Paragraph("Tổng tiền sau giảm: " + df.format(bill.getTotalAmount()) + " VND", boldFont));

    // Phương thức thanh toán
    document.add(new Paragraph("Phương thức thanh toán: " + bill.getPaymentMethod(), font));
    document.add(new Paragraph("Trạng thái thanh toán: " + bill.getPaymentStatus(), font));
    document.add(new Paragraph("Ghi chú: " + (bill.getNote() != null ? bill.getNote() : "Không có"), font));

    document.add(new Paragraph("\n")); // khoảng trắng
    document.add(new Paragraph("Cảm ơn quý khách!", titleFont));
}

}