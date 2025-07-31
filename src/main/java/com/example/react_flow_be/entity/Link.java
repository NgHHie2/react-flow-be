package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "links")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Link {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String edgeId; // Unique identifier for frontend
    
    @Column(length = 100)
    private String label;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LinkType linkType = LinkType.STRAIGHT;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArrowType sourceArrowType = ArrowType.NONE;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArrowType targetArrowType = ArrowType.ARROW;
    
    // Style
    @Column(nullable = false)
    private String strokeColor = "#b1b1b7";
    
    @Column(nullable = false)
    private Integer strokeWidth = 2;
    
    @Column(nullable = false)
    private String strokeStyle = "solid"; // solid, dashed, dotted
    
    @Column(nullable = false)
    private Boolean isAnimated = false;
    
    // Link points (can be shapes, texts, or specific points)
    @Column(nullable = false, length = 100)
    private String sourceNodeId; // Can reference Shape.nodeId or Text.nodeId
    
    @Column(nullable = false, length = 100)
    private String targetNodeId; // Can reference Shape.nodeId or Text.nodeId  
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NodeType sourceNodeType = NodeType.SHAPE;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NodeType targetNodeType = NodeType.SHAPE;
    
    // Handle positions (for custom Link points)
    @Column
    private String sourceHandle; // e.g., "top", "bottom", "left", "right"
    
    @Column
    private String targetHandle;
    
    // Path data for custom curves
    @Lob
    @Column
    private String pathData;
    
    // Z-index
    @Column
    private Integer zIndex = 0;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagram_id", nullable = false)
    @JsonBackReference("diagram-links")
    private Diagram diagram;
    
    // Join Links for complex diagrams
    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Joint> joints;
    
    public enum LinkType {
        STRAIGHT,    // Direct line
        BEZIER,      // Curved line
        STEP,        // Step line (L-shaped)
        SMOOTH_STEP, // Smooth step line
        CUSTOM       // Custom path
    }
    
    public enum ArrowType {
        NONE,           // No arrow
        ARROW,          // Simple arrow
        DIAMOND,        // Diamond
        CIRCLE,         // Circle
        SQUARE,         // Square
        TRIANGLE,       // Triangle
        CROW_FOOT       // Crow's foot (for ER diagrams)
    }
    
    public enum NodeType {
        SHAPE,
        TEXT,
        POINT    // Custom point
    }
}
