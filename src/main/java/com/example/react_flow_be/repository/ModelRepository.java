package com.example.react_flow_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.react_flow_be.entity.Model;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    Optional<Model> findByName(String name);
    Optional<Model> findByNodeId(String nodeId);
    
    @Query("SELECT m FROM Model m LEFT JOIN FETCH m.fields ORDER BY m.name")
    List<Model> findAllWithFields();
}

