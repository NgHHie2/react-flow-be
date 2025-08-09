package com.example.react_flow_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Attribute;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    List<Attribute> findByModelIdOrderByAttributeOrder(Long modelId);
    List<Attribute> findByIsForeignKeyTrue();
    Optional<Attribute> findByModelIdAndName(Long id, String attributeName);
}