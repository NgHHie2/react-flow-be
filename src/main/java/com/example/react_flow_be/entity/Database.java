package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Table(name = "databases")
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
    
    // Database-specific models
    @OneToMany(mappedBy = "database", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("database-models")
    private List<Model> models;
    
    // Database-specific migrations
    @OneToMany(mappedBy = "database", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("database-migrations")
    private List<Migration> migrations;
    
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
