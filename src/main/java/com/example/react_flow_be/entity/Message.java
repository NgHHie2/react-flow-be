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
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Lob
    @Column(nullable = false)
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT;
    
    @Column(length = 100)
    private String senderName; // Display name of sender
    
    @Column(length = 100)
    private String sessionId; // Session ID for anonymous users
    
    @Column(nullable = false)
    private Boolean isEdited = false;
    
    @Column(nullable = false)
    private Boolean isDeleted = false;
    
    // For replies/threads
    @Column
    private Long parentMessageId;
    
    // For file attachments or Connections
    @Column(length = 500)
    private String attachmentUrl;
    
    @Column(length = 100)
    private String attachmentType; // image, file, Connection, etc.
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    @JsonBackReference("chat-messages")
    private Chat chat;
    
    // Optional: Connection to user if authenticated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;
    
    public enum MessageType {
        TEXT,           // Regular text message
        IMAGE,          // Image message
        FILE,           // File attachment
        Connection,           // Connection/URL
        SYSTEM,         // System message (user joined, left, etc.)
        NOTIFICATION,   // Notification message
        DIAGRAM_Connection    // Connection to specific diagram element
    }
}