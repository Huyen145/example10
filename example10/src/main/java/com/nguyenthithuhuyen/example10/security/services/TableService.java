package com.nguyenthithuhuyen.example10.security.services;

import com.nguyenthithuhuyen.example10.entity.TableEntity;
import com.nguyenthithuhuyen.example10.entity.enums.Status;

import java.util.List;

public interface TableService {
    TableEntity createTable(TableEntity table);
    List<TableEntity> getAllTables();
    TableEntity getTableById(Long id);
    TableEntity updateTable(Long id, TableEntity table);
    void deleteTable(Long id);
    List<TableEntity> getTablesByStatus(Status status);
}
