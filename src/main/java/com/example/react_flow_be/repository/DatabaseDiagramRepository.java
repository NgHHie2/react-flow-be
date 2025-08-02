package com.example.react_flow_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.DatabaseDiagram;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatabaseDiagramRepository extends JpaRepository<DatabaseDiagram, Long> {
    List<DatabaseDiagram> findByFolderId(Long folderId);
    Optional<DatabaseDiagram> findByName(String name);
    
    @Query("SELECT d FROM DatabaseDiagram d WHERE d.isTemplate = true")
    List<DatabaseDiagram> findAllTemplates();
}