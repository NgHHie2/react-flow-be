package com.example.react_flow_be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.react_flow_be.entity.Connection;

@Repository  
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    List<Connection> findBySourceModelId(Long sourceModelId);
    List<Connection> findByTargetModelId(Long targetModelId);
    
    @Query("SELECT c FROM Connection c " +
           "JOIN FETCH c.sourceModel " +
           "JOIN FETCH c.targetModel")
    List<Connection> findAllWithModels();
}