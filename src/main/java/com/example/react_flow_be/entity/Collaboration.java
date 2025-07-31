package com.example.react_flow_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "collaborations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collaboration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollaborationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Permission permission;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column
    private LocalDateTime expiresAt; // Optional expiration date
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-collaborations")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    @JsonBackReference("folder-collaborations")
    private Folder folder;
    
    public enum CollaborationType {
        FOLDER_ACCESS,   // Access to entire folder
        DIAGRAM_ACCESS   // Access to specific diagram
    }
    
    public enum Permission {
        VIEW,           // Can only view
        COMMENT,        // Can view and comment
        EDIT,           // Can view, comment, and edit
        FULL_ACCESS     // Can view, edit, and manage permissions
    }
}
