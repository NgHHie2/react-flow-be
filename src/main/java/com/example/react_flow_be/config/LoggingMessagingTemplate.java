// LoggingMessagingTemplate.java - Wrapper to log all outgoing messages
package com.example.react_flow_be.config;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingMessagingTemplate {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public LoggingMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    public void convertAndSendToUser(String user, String destination, Object payload) {
        String fullDestination = "/user/" + user + destination;
        
        // log.info("🔍 OUTGOING MESSAGE DEBUG:");
        // log.info("  📍 Method: convertAndSendToUser");
        // log.info("  👤 User: {}", user);
        // log.info("  📫 Destination: {}", destination);  
        // log.info("  🎯 Full Path: {}", fullDestination);
        // log.info("  📦 Payload Type: {}", payload.getClass().getSimpleName());
        // log.info("  📄 Payload: {}", payload);
        
        try {
            messagingTemplate.convertAndSendToUser(user, destination, payload);
            // log.info("  ✅ Message sent successfully to {}", fullDestination);
        } catch (Exception e) {
            log.error("  ❌ Failed to send message to {}: {}", fullDestination, e.getMessage());
            throw e;
        }
        
        log.info("🔍 END MESSAGE DEBUG\n");
    }
    
    public void convertAndSend(String destination, Object payload) {
        log.info("🔍 TOPIC MESSAGE DEBUG:");
        log.info("  📫 Destination: {}", destination);
        log.info("  📦 Payload: {}", payload);
        
        try {
            messagingTemplate.convertAndSend(destination, payload);
            log.info("  ✅ Topic message sent successfully to {}", destination);
        } catch (Exception e) {
            log.error("  ❌ Failed to send topic message to {}: {}", destination, e.getMessage());
            throw e;
        }
        
        log.info("🔍 END TOPIC DEBUG\n");
    }
}