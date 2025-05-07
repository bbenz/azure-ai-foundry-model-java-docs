package com.azure.ai.foundry.util;

import com.azure.ai.projects.AIProjectClient;
import com.azure.ai.projects.models.Agent;
import com.azure.ai.projects.models.FileSearchTool;
import com.azure.ai.projects.models.FilePurpose;
import com.azure.ai.projects.models.Run;
import com.azure.ai.projects.models.Thread;
import com.azure.ai.projects.models.VectorStore;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Helper class for common agent operations.
 */
public class AgentHelper {

    private final AIProjectClient projectClient;
    
    public AgentHelper(AIProjectClient projectClient) {
        this.projectClient = projectClient;
    }
    
    /**
     * Creates a simple agent without any tools.
     *
     * @param model Model deployment name to use
     * @param instructions Instructions for the agent
     * @return The created agent
     */
    public Agent createSimpleAgent(String model, String instructions) {
        String agentName = "agent-" + UUID.randomUUID().toString().substring(0, 8);
        return projectClient.getAgents().createAgent(model, agentName, instructions);
    }
    
    /**
     * Creates an agent with file search capabilities.
     *
     * @param model Model deployment name to use
     * @param instructions Instructions for the agent
     * @param filePaths Paths to files to include in the search
     * @return The created agent with file search capabilities
     */
    public Agent createFileSearchAgent(String model, String instructions, List<Path> filePaths) {
        // Upload files
        List<String> fileIds = filePaths.stream()
            .map(path -> {
                try {
                    var file = projectClient.getAgents().uploadFile(path.toString(), FilePurpose.AGENTS);
                    return file.getId();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload file: " + path, e);
                }
            })
            .toList();
        
        // Create vector store
        String vectorStoreName = "vectorstore-" + UUID.randomUUID().toString().substring(0, 8);
        VectorStore vectorStore = projectClient.getAgents().createVectorStoreAndPoll(fileIds, vectorStoreName);
        
        // Create file search tool
        FileSearchTool fileSearch = new FileSearchTool(List.of(vectorStore.getId()));
        
        // Create agent
        String agentName = "search-agent-" + UUID.randomUUID().toString().substring(0, 8);
        Agent agent = projectClient.getAgents().createAgent(
            model,
            agentName,
            instructions,
            fileSearch.getDefinitions(),
            fileSearch.getResources());
        
        // Store vector store ID in agent description for cleanup
        agent.setDescription("VectorStoreId:" + vectorStore.getId() + ";FileIds:" + String.join(",", fileIds));
        
        return agent;
    }
    
    /**
     * Runs a simple conversation with an agent.
     *
     * @param agentId The agent ID
     * @param userMessage The user message to send
     * @return The agent's response message
     */
    public String runConversation(String agentId, String userMessage) {
        // Create thread
        Thread thread = projectClient.getAgents().createThread();
        
        // Add user message
        projectClient.getAgents().createMessage(thread.getId(), "user", userMessage);
        
        // Run the agent
        Run run = projectClient.getAgents().createAndProcessRun(thread.getId(), agentId);
        
        if ("failed".equals(run.getStatus())) {
            return "Error: " + run.getLastError();
        }
        
        // Get the agent's response
        var messages = projectClient.getAgents().listMessages(thread.getId());
        var lastMsg = messages.getLastTextMessageByRole("assistant");
        
        return lastMsg != null ? lastMsg.getText().getValue() : "No response";
    }
    
    /**
     * Cleans up an agent and associated resources.
     *
     * @param agent The agent to clean up
     */
    public void cleanupAgent(Agent agent) {
        // First, delete the agent
        projectClient.getAgents().deleteAgent(agent.getId());
        
        // If the agent has a description with vector store and file IDs, clean those up too
        if (agent.getDescription() != null && agent.getDescription().startsWith("VectorStoreId:")) {
            String description = agent.getDescription();
            
            // Extract vector store ID
            int vectorStoreIdStart = description.indexOf("VectorStoreId:") + "VectorStoreId:".length();
            int vectorStoreIdEnd = description.indexOf(";", vectorStoreIdStart);
            if (vectorStoreIdEnd > vectorStoreIdStart) {
                String vectorStoreId = description.substring(vectorStoreIdStart, vectorStoreIdEnd);
                try {
                    projectClient.getAgents().deleteVectorStore(vectorStoreId);
                } catch (Exception e) {
                    System.err.println("Failed to delete vector store: " + e.getMessage());
                }
            }
            
            // Extract file IDs
            int fileIdsStart = description.indexOf("FileIds:") + "FileIds:".length();
            if (fileIdsStart > "FileIds:".length()) {
                String fileIdsStr = description.substring(fileIdsStart);
                String[] fileIds = fileIdsStr.split(",");
                for (String fileId : fileIds) {
                    try {
                        projectClient.getAgents().deleteFile(fileId);
                    } catch (Exception e) {
                        System.err.println("Failed to delete file: " + e.getMessage());
                    }
                }
            }
        }
    }
}
