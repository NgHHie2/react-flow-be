package com.example.react_flow_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Model;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    Optional<Model> findByName(String name);
    Optional<Model> findByNodeId(String nodeId);
    List<Model> findByDatabaseDiagramId(Long databaseDiagramId);
    Optional<Model> findByNodeIdAndDatabaseDiagram_Id(String nodeId, Long databaseDiagramId);

}