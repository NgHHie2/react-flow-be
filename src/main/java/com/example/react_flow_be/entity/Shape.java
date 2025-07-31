package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shapes")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shape {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String nodeId; // Unique identifier for frontend
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShapeType type;
    
    // Position
    @Column(nullable = false)
    private Double positionX = 0.0;
    
    @Column(nullable = false)
    private Double positionY = 0.0;
    
    // Size
    @Column(nullable = false)
    private Double width = 100.0;
    
    @Column(nullable = false)
    private Double height = 60.0;
    
    // Style
    @Column(nullable = false)
    private String backgroundColor = "#ffffff";
    
    @Column(nullable = false)
    private String borderColor = "#000000";
    
    @Column
    private Integer borderWidth = 1;
    
    @Column
    private String borderStyle = "solid";
    
    @Column
    private Integer borderRadius = 0;
    
    // Z-index for layering
    @Column
    private Integer zIndex = 0;
    
    // Rotation
    @Column
    private Double rotation = 0.0;
    
    // Custom properties (JSON)
    @Lob
    @Column
    private String customProperties;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagram_id", nullable = false)
    @JsonBackReference("diagram-shapes")
    private Diagram diagram;
    
    // Parent-child relationship for grouping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_shape_id")
    @JsonBackReference("parent-children-shapes")
    private Shape parentShape;
    
    @OneToMany(mappedBy = "parentShape", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("parent-children-shapes")
    private List<Shape> childShapes;
    
    public enum ShapeType {
        // Basic shapes
        RECTANGLE,
        CIRCLE,
        ELLIPSE,
        TRIANGLE,
        DIAMOND,
        POLYGON,
        
        // Flowchart shapes
        PROCESS,
        DECISION,
        START_END,
        CONNECTOR,
        
        // Database shapes
        TABLE,
        VIEW,
        STORED_PROCEDURE,
        
        // UML shapes
        CLASS,
        INTERFACE,
        PACKAGE,
        
        // Custom
        CUSTOM,
        
        // Container
        GROUP
    }
}