package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Table(name = "models")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Model extends Shape {
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModelType modelType = ModelType.TABLE;
    
    // Database reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "database_id", nullable = false)
    @JsonBackReference("database-models")
    private Database database;
    
    // Fields in this model
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("model-fields")
    private List<Field> fields;
    
    // Source relationships (this model is the source)
    @OneToMany(mappedBy = "sourceModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("source-relationships")
    private List<Connection> sourceRelationships;
    
    // Target relationships (this model is the target)
    @OneToMany(mappedBy = "targetModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("target-relationships")
    private List<Connection> targetRelationships;
    
    public enum ModelType {
        TABLE,           // Database table
        VIEW,            // Database view
        STORED_PROCEDURE,// Stored procedure
        FUNCTION,        // Database function
        TRIGGER,         // Database trigger
        INDEX,           // Database index
        SEQUENCE,        // Database sequence
        CUSTOM          // Custom model type
    }
}