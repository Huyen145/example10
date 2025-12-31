package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.entity.TableEntity;
import com.nguyenthithuhuyen.example10.entity.enums.Status;
import com.nguyenthithuhuyen.example10.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tables")
public class TableController {

    private final TableRepository tableRepository;

    // -------------------------------------------------------------------------
    // 1. CHỨC NĂNG TẠO BÀN (CREATE)
    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TableEntity> create(@RequestBody TableEntity table) {
        // Đảm bảo số bàn (tableNumber) không bị null
        if (table.getTableNumber() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // Mặc định bàn là FREE khi mới tạo
        table.setStatus(Status.FREE);
        table.setCreatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());
        
        // Lưu và trả về đối tượng đã tạo
        return ResponseEntity.status(HttpStatus.CREATED).body(tableRepository.save(table));
    }

    // -------------------------------------------------------------------------
    // 2. CHỨC NĂNG ĐỌC BÀN (READ)

    // Lấy tất cả bàn
    @GetMapping
    public ResponseEntity<List<TableEntity>> getAll() {
        return ResponseEntity.ok(tableRepository.findAll());
    }

    // Lấy bàn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<TableEntity> getById(@PathVariable Long id) {
        return tableRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Lấy tất cả bàn đang FREE (Có thể dùng cho Nhân viên chọn bàn)
    @GetMapping("/available")
    public ResponseEntity<List<TableEntity>> getAvailableTables() {
        // Giả định TableRepository có phương thức findByStatus
        return ResponseEntity.ok(tableRepository.findByStatus(Status.FREE));
    }

    // -------------------------------------------------------------------------
    // 3. CHỨC NĂNG CẬP NHẬT BÀN (UPDATE)
    // ADMIN hoặc MODERATOR (Nhân viên) đều có thể cập nhật trạng thái bàn
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<TableEntity> update(@PathVariable Long id, @RequestBody TableEntity tableUpdateData) {
        TableEntity existingTable = tableRepository.findById(id)
                .orElse(null);

        if (existingTable == null) {
            return ResponseEntity.notFound().build();
        }

        // Chỉ Admin/Moderator mới được phép thay đổi Capacity và Number
        // Nhân viên thường chỉ cập nhật Status
        
        // Cập nhật số bàn (chỉ ADMIN nên dùng)
        if (tableUpdateData.getTableNumber() != null) {
            existingTable.setTableNumber(tableUpdateData.getTableNumber());
        }
        // Cập nhật sức chứa (chỉ ADMIN nên dùng)
        if (tableUpdateData.getCapacity() != null) {
            existingTable.setCapacity(tableUpdateData.getCapacity());
        }
        // Cập nhật trạng thái (quan trọng cho cả ADMIN và MODERATOR)
        if (tableUpdateData.getStatus() != null) {
            existingTable.setStatus(tableUpdateData.getStatus());
        }
        
        existingTable.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(tableRepository.save(existingTable));
    }

    // -------------------------------------------------------------------------
    // 4. CHỨC NĂNG XÓA BÀN (DELETE)
    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!tableRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Cần kiểm tra logic: Không cho phép xóa bàn đang ở trạng thái OCCUPIED/SERVING
        TableEntity table = tableRepository.findById(id).get();
        if (table.getStatus() != Status.FREE) {
             // 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); 
        }
        
        tableRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}