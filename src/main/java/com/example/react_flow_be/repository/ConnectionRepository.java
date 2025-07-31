package com.example.react_flow_be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.react_flow_be.entity.Connection;

@Repository  
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    List<Connection> findBySourceNodeId(String sourceNodeId);
    List<Connection> findByTargetNodeId(String targetNodeId);
    List<Connection> findByDiagramId(Long diagramId);
}