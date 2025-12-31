package com.nguyenthithuhuyen.example10.repository;

import com.nguyenthithuhuyen.example10.entity.TableEntity;
import com.nguyenthithuhuyen.example10.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long> {
    List<TableEntity> findByStatus(Status status);
    TableEntity findByNumber(Integer number);
}
