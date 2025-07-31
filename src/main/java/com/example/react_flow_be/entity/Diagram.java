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
@Table(name = "diagrams")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diagram {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiagramType type;
    
    @Column(nullable = false)
    private Boolean isPublic = false;
    
    @Column(nullable = false)
    private Boolean isTemplate = false;
    
    // Canvas settings
    @Lob
    @Column
    private String canvasData; // JSON string chứa toàn bộ canvas data
    
    @Column
    private Double zoomLevel = 1.0;
    
    @Column
    private Double panX = 0.0;
    
    @Column
    private Double panY = 0.0;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    @JsonBackReference("folder-diagrams")
    private Folder folder;
    
    @OneToMany(mappedBy = "diagram", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("diagram-shapes")
    private List<Shape> shapes;
    
    @OneToMany(mappedBy = "diagram", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("diagram-connections")
    private List<Connection> connections;
    
    @OneToMany(mappedBy = "diagram", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("diagram-texts")
    private List<Text> texts;
    
    @OneToMany(mappedBy = "diagram", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("diagram-history")
    private List<History> histories;
    
    public enum DiagramType {
        ER_DIAGRAM,        // Entity-Relationship Diagram
        FLOWCHART,         // Process Flow Diagram
        UML_CLASS,         // UML Class Diagram
        NETWORK,           // Network Diagram
        ORGANIZATIONAL,    // Org Chart
        MIND_MAP,          // Mind Map
        WIREFRAME,         // UI Wireframe
        CUSTOM            // Custom diagram
    }
}
