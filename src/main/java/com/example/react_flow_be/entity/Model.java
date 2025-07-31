package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
    
    // BỎ database reference - chỉ dùng diagram từ Shape
    // Database relationship sẽ được handle qua Diagram
    
    // Fields in this model
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("model-fields")
    private List<Field> fields;
    
    // BỎ connection relationships - để Connection tự handle
    // Nếu cần thì query từ Connection entity
    
    // Helper method để get Database
    public Database getDatabase() {
        Diagram diagram = super.getDiagram();
        return (diagram instanceof Database) ? (Database) diagram : null;
    }
    
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