package com.example.react_flow_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Shape;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShapeRepository extends JpaRepository<Shape, Long> {
    List<Shape> findByDiagramId(Long diagramId);
    Optional<Shape> findByNodeId(String nodeId);
    List<Shape> findByType(Shape.ShapeType type);
    
    @Query("SELECT s FROM Shape s WHERE s.diagram.id = :diagramId ORDER BY s.zIndex ASC")
    List<Shape> findByDiagramIdOrderByZIndex(Long diagramId);
}