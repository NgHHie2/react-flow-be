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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String content;
    private MessageType messageType;
    private String senderName;
    private String sessionId;
    private Boolean isEdited;
    private Boolean isDeleted;
    private Long parentMessageId;
    private String attachmentUrl;
    private String attachmentType;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    
    public enum MessageType {
        TEXT, IMAGE, FILE, LINK, SYSTEM, NOTIFICATION, DIAGRAM_LINK
    }
}