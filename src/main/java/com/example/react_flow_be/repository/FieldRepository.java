package com.example.react_flow_be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Field;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {
    List<Field> findByModelIdOrderByFieldOrder(Long modelId);
    List<Field> findByIsForeignKeyTrue();
}