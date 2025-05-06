package com.example.ai.projects.langchain;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.ConnectionsClient;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.ai.projects.models.Connection;
import com.azure.ai.projects.models.Deployment;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.Map;

/**
 * Sample demonstrating tool usage with LangChain4j and Azure SDK for Java.
 */
public class AgentToolUsageSample {

    // Define an agent interface with tool-calling capabilities
    interface AIProjectAgent {
        @SystemMessage("You are an AI assistant with expertise in Azure SDK for Java. " +
                     "Use the available tools to help answer questions.")
        @UserMessage("{{message}}")
        String chat(String message);
    }    /**
     * Main method to demonstrate using LangChain4j with Azure SDK for Java for tool-calling.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Get environment variables
        String endpoint = Configuration.getGlobalConfiguration().get("AZURE_AI_ENDPOINT", "your-ai-endpoint");
        String deploymentName = Configuration.getGlobalConfiguration().get("DEPLOYMENT_NAME", "your-deployment-name");
        String apiKey = Configuration.getGlobalConfiguration().get("AZURE_API_KEY", "your-api-key");
        
        try {
            // Create Azure OpenAI chat model
            AzureOpenAiChatModel model = AzureOpenAiChatModel.builder()
                .endpoint(endpoint)
                .apiKey(apiKey)
                .deploymentName(deploymentName)
                .build();
            
            // Create the tools provider
            AIProjectToolProvider toolProvider = new AIProjectToolProvider(endpoint);
            
            // Create an AI agent with tool-calling capabilities
            AIProjectAgent agent = AiServices.builder(AIProjectAgent.class)
                .chatLanguageModel(model)
                .tools(toolProvider)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
            
            // Chat with the agent
            String response = agent.chat("List all my available connections in Azure SDK for Java");
            System.out.println("Agent response: " + response);
            
            response = agent.chat("Tell me about my gpt-4o deployment");
            System.out.println("Agent response: " + response);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * A tools provider class that integrates with Azure SDK for Java.
     */
    static class AIProjectToolProvider {
        private final ConnectionsClient connectionsClient;
        private final DeploymentsClient deploymentsClient;
        
        public AIProjectToolProvider(String endpoint) {
            AIProjectClientBuilder builder = new AIProjectClientBuilder()
                .endpoint(endpoint)
                .credential(new DefaultAzureCredentialBuilder().build());
                
            this.connectionsClient = builder.buildConnectionsClient();
            this.deploymentsClient = builder.buildDeploymentsClient();
        }
        
        @Tool("Lists all connections available in Azure SDK for Java")
        public String listConnections() {
            StringBuilder result = new StringBuilder("Available connections:\n");
              for (Connection connection : connectionsClient.list(null, null)) {
                result.append("- ").append(connection.getName())
                      .append(" (Type: ").append(connection.getType().getValue()).append(")\n");
            }
            
            return result.toString();
        }
        
        @Tool("Gets information about a specific deployment by name")
        public String getDeploymentInfo(String deploymentName) {
            try {
                Deployment deployment = deploymentsClient.get(deploymentName);
                StringBuilder info = new StringBuilder("Deployment information:\n");
                info.append("- Name: ").append(deployment.getName()).append("\n");
                info.append("- Type: ").append(deployment.getType().getValue()).append("\n");
                info.append("- Created at: ").append(deployment.getCreatedAt()).append("\n");
                
                // Add metadata if available
                Map<String, String> metadata = deployment.getMetadata();
                if (metadata != null && !metadata.isEmpty()) {
                    info.append("- Metadata:\n");
                    for (Map.Entry<String, String> entry : metadata.entrySet()) {
                        info.append("  - ").append(entry.getKey()).append(": ")
                            .append(entry.getValue()).append("\n");
                    }
                }
                
                return info.toString();
            } catch (Exception e) {
                return "Error getting deployment info: " + e.getMessage();
            }
        }
    }
}
