package com.nguyenthithuhuyen.example10.security.services.impl;

import com.nguyenthithuhuyen.example10.entity.TableEntity;
import com.nguyenthithuhuyen.example10.entity.enums.Status;
import com.nguyenthithuhuyen.example10.repository.TableRepository;
import com.nguyenthithuhuyen.example10.security.services.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;

    @Override
    public TableEntity createTable(TableEntity table) {
        return tableRepository.save(table);
    }

    @Override
    public List<TableEntity> getAllTables() {
        return tableRepository.findAll();
    }

    @Override
    public TableEntity getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));
    }

    @Override
    public TableEntity updateTable(Long id, TableEntity table) {
        TableEntity existing = getTableById(id);
        existing.setNumber(table.getNumber());
        existing.setCapacity(table.getCapacity());
        existing.setStatus(table.getStatus());
        return tableRepository.save(existing);
    }

    @Override
    public void deleteTable(Long id) {
        if (!tableRepository.existsById(id)) {
            throw new RuntimeException("Table not found with id: " + id);
        }
        tableRepository.deleteById(id);
    }

    @Override
    public List<TableEntity> getTablesByStatus(Status status) {
        return tableRepository.findByStatus(status);
    }
    public boolean isTableAvailable(Long tableId) {
    TableEntity table = tableRepository.findById(tableId)
        .orElseThrow(() -> new RuntimeException("Table not found"));
    return table.getStatus() == Status.FREE;
}

}
