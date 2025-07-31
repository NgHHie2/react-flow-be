package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Table(name = "database_diagrams")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Database extends Diagram {
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DatabaseType databaseType = DatabaseType.MYSQL;
    
    @Column(length = 20)
    private String version;
    
    @Column(length = 50)
    private String charset = "utf8mb4";
    
    @Column(length = 50)
    private String collation = "utf8mb4_unicode_ci";
    
    // BỎ models relationship - dùng shapes từ Diagram
    // Models sẽ được query từ shapes với instanceof check
    
    // Database-specific migrations
    @OneToMany(mappedBy = "database", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("database-migrations")
    private List<Migration> migrations;
    
    // Helper method để get Models từ shapes
    public List<Model> getModels() {
        return getShapes().stream()
            .filter(shape -> shape instanceof Model)
            .map(shape -> (Model) shape)
            .toList();
    }
    
    public enum DatabaseType {
        MYSQL,
        POSTGRESQL,
        ORACLE,
        SQL_SERVER,
        SQLITE,
        MONGODB,
        REDIS,
        CASSANDRA,
        CUSTOM
    }
}