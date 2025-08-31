package com.example.react_flow_be.config;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Component
@Slf4j
public class WebSocketSessionManager {
    private final Set<String> activeSessions = ConcurrentHashMap.newKeySet();
    
    public void addSession(String sessionId) {
        activeSessions.add(sessionId);
        log.info("Added session: {}, Total: {}", sessionId, activeSessions.size());
    }
    
    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
        log.info("Removed session: {}, Total: {}", sessionId, activeSessions.size());
    }
    
    public Set<String> getActiveSessionsExcept(String excludeSessionId) {
        Set<String> result = activeSessions.stream()
            .filter(sessionId -> !sessionId.equals(excludeSessionId))
            .collect(Collectors.toSet());
        
        log.info("Active sessions: {}, Excluding: {}, Result: {}", 
            activeSessions.size(), excludeSessionId, result.size());
        
        return result;
    }
    
    public int getTotalSessions() {
        return activeSessions.size();
    }
    
    public Set<String> getAllSessions() {
        return new HashSet<>(activeSessions);
    }
}