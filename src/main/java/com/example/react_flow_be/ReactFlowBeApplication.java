package com.example.react_flow_be;

import com.example.react_flow_be.service.SchemaVisualizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class ReactFlowBeApplication implements CommandLineRunner {

    private final SchemaVisualizerService schemaVisualizerService;

    public static void main(String[] args) {
        SpringApplication.run(ReactFlowBeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("Checking existing data...");
            
            // Kiểm tra xem đã có data chưa
            var existingData = schemaVisualizerService.getSchemaData();
            
            if (existingData.getModels().isEmpty()) {
                System.out.println("No existing data found. Initializing sample data...");
                schemaVisualizerService.initializeSampleData();
                System.out.println("Sample data initialized successfully!");
            } else {
                System.out.println("Found existing data:");
                System.out.println("- Models: " + existingData.getModels().size());
                System.out.println("- Connections: " + existingData.getConnections().size());
                System.out.println("Skipping sample data initialization.");
            }
            
            System.out.println("=".repeat(50));
            System.out.println("MySQL Database: schema_visualizer");
            System.out.println("API Endpoint: http://localhost:8080/api/schema");
            System.out.println("Health Check: http://localhost:8080/api/schema/health");
            System.out.println("Initialize Data: POST http://localhost:8080/api/schema/initialize");
            System.out.println("=".repeat(50));
            
        } catch (Exception e) {
            System.err.println("Error during startup: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}