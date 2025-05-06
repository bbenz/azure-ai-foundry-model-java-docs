package com.example.ai.projects.langchain;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.ai.projects.models.Deployment;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;

/**
 * Sample demonstrating integration of Azure SDK for Java with LangChain4j.
 */
public class LangChainIntegrationSample {

    // Define an assistant interface
    interface Assistant {
        @UserMessage("{{message}}")
        String chat(String message);    }

    /**
     * Main method to demonstrate Azure SDK for Java integration with LangChain4j.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Get environment variables
        String endpoint = Configuration.getGlobalConfiguration().get("AZURE_AI_ENDPOINT", "your-ai-endpoint");
        String deploymentName = Configuration.getGlobalConfiguration().get("DEPLOYMENT_NAME", "your-deployment-name");
        String apiKey = Configuration.getGlobalConfiguration().get("AZURE_API_KEY", "your-api-key");
        
        // First, use Azure SDK for Java to get deployment information
        DeploymentsClient deploymentsClient = new AIProjectClientBuilder()
            .endpoint(endpoint)
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildDeploymentsClient();
        
        try {
            // Get the deployment
            Deployment deployment = deploymentsClient.get(deploymentName);
            System.out.printf("Using deployment: %s (Type: %s)%n", 
                deployment.getName(), 
                deployment.getType().getValue());
                
            // Now use LangChain4j with the deployment information
            AzureOpenAiChatModel model = AzureOpenAiChatModel.builder()
                .endpoint(endpoint)
                .apiKey(apiKey)
                .deploymentName(deploymentName)
                .build();
                
            // Create an AI assistant using LangChain4j
            Assistant assistant = AiServices.create(Assistant.class, model);
            
            // Chat with the assistant
            String response = assistant.chat("Tell me about Azure SDK for Java");
            System.out.println("Assistant response: " + response);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
