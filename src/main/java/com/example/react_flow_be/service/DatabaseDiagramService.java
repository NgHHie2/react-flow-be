package com.example.react_flow_be.service;

import com.example.react_flow_be.entity.*;
import com.example.react_flow_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DatabaseDiagramService {
    
    private final DatabaseDiagramRepository databaseDiagramRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<DatabaseDiagram> getAllDatabaseDiagrams() {
        return databaseDiagramRepository.findAll();
    }
    
    @Transactional
    public DatabaseDiagram createSampleDatabaseDiagram(Folder folder) {
        DatabaseDiagram databaseDiagram = new DatabaseDiagram();
        databaseDiagram.setName("Blog System");
        databaseDiagram.setDescription("Sample blog system database schema");
        databaseDiagram.setType(Diagram.DiagramType.ER_DIAGRAM);
        databaseDiagram.setDatabaseType(DatabaseDiagram.DatabaseType.MYSQL);
        databaseDiagram.setVersion("8.0");
        databaseDiagram.setCharset("utf8mb4");
        databaseDiagram.setCollation("utf8mb4_unicode_ci");
        databaseDiagram.setFolder(folder);
        databaseDiagram.setIsPublic(false);
        databaseDiagram.setIsTemplate(true);
        databaseDiagram.setZoomLevel(1.0);
        databaseDiagram.setPanX(0.0);
        databaseDiagram.setPanY(0.0);
        return databaseDiagramRepository.save(databaseDiagram);
    }
    
    public User createSampleUser() {
        User user = new User();
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setPassword("password");
        user.setFullName("System Administrator");
        user.setRole(User.UserRole.ADMIN);
        return userRepository.save(user);
    }
    
    public Folder createSampleFolder(User owner) {
        Folder folder = new Folder();
        folder.setName("Sample ER Diagrams");
        folder.setDescription("Sample folder containing ER diagram examples");
        folder.setOwner(owner);
        folder.setColor("#3d5787");
        return folderRepository.save(folder);
    }
}
