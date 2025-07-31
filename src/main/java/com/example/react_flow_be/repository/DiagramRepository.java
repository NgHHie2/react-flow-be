package com.example.react_flow_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Diagram;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagramRepository extends JpaRepository<Diagram, Long> {
    List<Diagram> findByFolderId(Long folderId);
    List<Diagram> findByType(Diagram.DiagramType type);
    Optional<Diagram> findByName(String name);
    
    @Query("SELECT d FROM Diagram d WHERE d.isPublic = true")
    List<Diagram> findAllPublic();
    
    @Query("SELECT d FROM Diagram d WHERE d.isTemplate = true")
    List<Diagram> findAllTemplates();
}