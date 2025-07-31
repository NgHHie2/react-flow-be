package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "joints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Joint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Double positionX;
    
    @Column(nullable = false)
    private Double positionY;
    
    @Column(nullable = false)
    private Integer orderIndex; // Order of joint points in the Link
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JointType type = JointType.WAYPOINT;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "link_id", nullable = false)
    @JsonBackReference
    private Link link;
    
    public enum JointType {
        WAYPOINT,      // Simple waypoint
        CONTROL_POINT, // Bezier control point
        ANCHOR_POINT   // Anchor point for complex curves
    }
}