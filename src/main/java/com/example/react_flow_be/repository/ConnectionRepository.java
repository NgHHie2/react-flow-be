package com.example.react_flow_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Connection;

@Repository  
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    Optional<Connection> findByFieldId(Long fieldId);
    List<Connection> findByTargetModelId(Long targetModelId);
}