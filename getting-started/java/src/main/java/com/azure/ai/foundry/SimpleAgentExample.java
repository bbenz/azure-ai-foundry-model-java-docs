package com.azure.ai.foundry;

import com.azure.ai.foundry.util.AgentHelper;
import com.azure.ai.foundry.util.EnvironmentUtil;
import com.azure.ai.projects.AIProjectClient;
import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.models.Agent;
import com.azure.identity.DefaultAzureCredentialBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Sample demonstrating how to use the AgentHelper utility class.
 */
public class SimpleAgentExample {
    
    public static void main(String[] args) {
        try {
            // Check required environment variables
            if (!EnvironmentUtil.checkRequiredEnv("AZURE_AI_ENDPOINT", "MODEL_DEPLOYMENT_NAME")) {
                System.err.println("Please set all required environment variables in your .env file");
                System.exit(1);
            }
            
            // Initialize AI Project client
            String endpoint = EnvironmentUtil.getEnv("AZURE_AI_ENDPOINT");
            String modelDeploymentName = EnvironmentUtil.getEnv("MODEL_DEPLOYMENT_NAME");
            
            AIProjectClient projectClient = new AIProjectClientBuilder()
                .endpoint(endpoint)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
            
            // Create AgentHelper
            AgentHelper agentHelper = new AgentHelper(projectClient);
            
            // Demonstrate simple agent
            System.out.println("\n=== Simple Agent Example ===");
            Agent simpleAgent = agentHelper.createSimpleAgent(
                modelDeploymentName,
                "You are a helpful assistant who explains complex topics in simple terms.");
            
            System.out.println("Created simple agent with ID: " + simpleAgent.getId());
            
            try {
                // Run a conversation
                String response = agentHelper.runConversation(
                    simpleAgent.getId(),
                    "Explain how GPT models work in terms a 10-year-old would understand.");
                
                System.out.println("\nAgent response:");
                System.out.println(response);
                
            } finally {
                // Clean up
                agentHelper.cleanupAgent(simpleAgent);
                System.out.println("Cleaned up simple agent");
            }
            
            // Demonstrate file search agent
            System.out.println("\n=== File Search Agent Example ===");
            
            // Create a test file
            Path testFilePath = createTestFile();
            
            try {
                Agent searchAgent = agentHelper.createFileSearchAgent(
                    modelDeploymentName,
                    "You are a helpful assistant that can search for information in files. " +
                    "Always cite your sources when you find information in the documents.",
                    List.of(testFilePath));
                
                System.out.println("Created file search agent with ID: " + searchAgent.getId());
                
                try {
                    // Run a conversation with the search agent
                    String response = agentHelper.runConversation(
                        searchAgent.getId(),
                        "What is the capital of France according to the document?");
                    
                    System.out.println("\nAgent response:");
                    System.out.println(response);
                    
                } finally {
                    // Clean up
                    agentHelper.cleanupAgent(searchAgent);
                    System.out.println("Cleaned up file search agent");
                }
                
            } finally {
                // Delete the test file
                Files.deleteIfExists(testFilePath);
            }
            
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a test file with some information.
     */
    private static Path createTestFile() throws Exception {
        String content = """
            # World Capitals Information
            
            ## European Capitals
            
            - France: Paris
            - Germany: Berlin
            - Italy: Rome
            - Spain: Madrid
            - United Kingdom: London
            
            ## North American Capitals
            
            - United States: Washington, D.C.
            - Canada: Ottawa
            - Mexico: Mexico City
            
            ## Asian Capitals
            
            - Japan: Tokyo
            - China: Beijing
            - India: New Delhi
            - South Korea: Seoul
            """;
        
        Path path = Paths.get("world_capitals.md");
        Files.writeString(path, content);
        return path;
    }
}
