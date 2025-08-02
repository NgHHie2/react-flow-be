package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nodeId;
    private String name;
    private ModelType modelType;
    
    // Position & Size
    private Double positionX;
    private Double positionY;
    private Double width;
    private Double height;
    
    // Style
    private String backgroundColor;
    private String borderColor;
    private Integer borderWidth;
    private String borderStyle;
    private Integer borderRadius;
    
    private Integer zIndex;
    private Double rotation;
    private String customProperties;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "database_diagram_id")
    private DatabaseDiagram databaseDiagram;
    
    @OneToMany(mappedBy = "model")
    private List<Field> fields;
    
    public enum ModelType {
        TABLE, VIEW, STORED_PROCEDURE, FUNCTION, TRIGGER, INDEX, SEQUENCE, CUSTOM
    }
}
