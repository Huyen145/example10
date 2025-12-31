package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.entity.Bill;
import com.nguyenthithuhuyen.example10.payload.request.BillRequest;
import com.nguyenthithuhuyen.example10.security.services.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    // ✅ Tạo hóa đơn: Cho phép Nhân viên và Admin
    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')") // SỬA ĐỔI QUYỀN TRUY CẬP
    public ResponseEntity<?> createBill(@RequestBody BillRequest request) {
        try {
            Bill createdBill = billService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBill);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi tạo hóa đơn: " + e.getMessage());
        }
    }

    // ✅ Lấy danh sách hóa đơn: Chỉ Staff/Admin được xem
    @GetMapping
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')") // BỔ SUNG QUYỀN TRUY CẬP
    public ResponseEntity<?> getAllBills() {
        return ResponseEntity.ok(billService.getAll());
    }

    // ✅ Lấy hóa đơn theo ID: Chỉ Staff/Admin được xem
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')") // BỔ SUNG QUYỀN TRUY CẬP
    public ResponseEntity<?> getBillById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(billService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ✅ Cập nhật hóa đơn: Cho phép Nhân viên và Admin
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')") // SỬA ĐỔI QUYỀN TRUY CẬP
    public ResponseEntity<?> updateBill(@PathVariable Long id, @RequestBody Bill bill) {
        try {
            return ResponseEntity.ok(billService.update(id, bill));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật hóa đơn: " + e.getMessage());
        }
    }

    // ✅ Xóa hóa đơn: Chỉ Admin được xóa
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // BỔ SUNG QUYỀN TRUY CẬP
    public ResponseEntity<?> deleteBill(@PathVariable Long id) {
        billService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Xuất hóa đơn ra PDF: Cho phép Staff/Admin
    @GetMapping("/{id}/export")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')") // BỔ SUNG QUYỀN TRUY CẬP
    public ResponseEntity<?> exportBillToPdf(@PathVariable Long id) {
        try {
            // Tối ưu: Chỉ truyền ID, để Service tìm và tạo PDF
            byte[] pdfBytes = billService.exportToPdfBytes(id); 
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hoadon_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (RuntimeException e) {
            // Lỗi khi không tìm thấy hóa đơn
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Không tìm thấy hóa đơn ID " + id + ".");
        } catch (Exception e) {
            // Lỗi kỹ thuật khi tạo PDF
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Lỗi khi xuất PDF: " + e.getMessage());
        }
    }
}