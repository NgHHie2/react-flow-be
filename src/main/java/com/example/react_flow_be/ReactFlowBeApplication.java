package com.example.react_flow_be;

import com.example.react_flow_be.service.SchemaVisualizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@RequiredArgsConstructor
public class ReactFlowBeApplication implements CommandLineRunner {

    private final SchemaVisualizerService schemaVisualizerService;
    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(ReactFlowBeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("=".repeat(60));
            System.out.println("🚀 React Flow Backend Application Started");
            System.out.println("=".repeat(60));
            
            

            System.out.println("🗑️  Force reset detected. Clearing all existing data...");
            schemaVisualizerService.clearAllData();
            System.out.println("✅ All data cleared successfully!");
            
            System.out.println("🔄 Initializing fresh sample data...");
            schemaVisualizerService.initializeSampleData();
            System.out.println("✅ Sample data initialized successfully!");
            // Check existing data
            System.out.println("🔍 Checking existing data...");
            var existingData = schemaVisualizerService.getSchemaData();
            
            if (existingData.getModels().isEmpty()) {
                System.out.println("📭 No existing data found. Initializing sample data...");
                schemaVisualizerService.initializeSampleData();
                System.out.println("✅ Sample data initialized successfully!");
            } else {
                System.out.println("📊 Found existing data:");
                System.out.println("   - Database: " + existingData.getName());
                System.out.println("   - Models: " + existingData.getModels().size());
                System.out.println("   - Type: " + existingData.getDatabaseType());
                System.out.println("⏭️  Skipping sample data initialization.");
            }
            
            
            System.out.println("=".repeat(60));
            System.out.println("📋 Application Information:");
            System.out.println("   MySQL Database: schema_visualizer");
            System.out.println("   API Endpoint: http://localhost:8080/api/schema");
            System.out.println("   Health Check: http://localhost:8080/api/schema/health");
            System.out.println("   Initialize Data: POST http://localhost:8080/api/schema/initialize");
            System.out.println("   Clear Data: POST http://localhost:8080/api/schema/clear");
            
            // Show active profiles
            String[] activeProfiles = environment.getActiveProfiles();
            if (activeProfiles.length > 0) {
                System.out.println("   Active Profiles: " + String.join(", ", activeProfiles));
            }
            
            System.out.println("=".repeat(60));
            System.out.println("🎯 Application ready for connections!");
            System.out.println("=".repeat(60));
            
        } catch (Exception e) {
            System.err.println("❌ Error during startup: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
}