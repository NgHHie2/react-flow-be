package com.example.react_flow_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.react_flow_be.entity.Database;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatabaseRepository extends JpaRepository<Database, Long> {
    List<Database> findByFolderId(Long folderId);
    Optional<Database> findByName(String name);
    
    @Query("SELECT d FROM Database d WHERE d.isTemplate = true")
    List<Database> findAllTemplates();
}