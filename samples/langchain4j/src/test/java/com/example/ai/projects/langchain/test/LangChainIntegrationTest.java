package com.example.ai.projects.langchain.test;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.service.AiServices;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

interface SimpleAssistant {
    String chat(String message);
}

public class LangChainIntegrationTest {
    private static DeploymentsClient deploymentsClient;    private static String deploymentName;
    private static String endpoint;
    private static String apiKey;
    
    @BeforeAll
    public static void setup() {
        endpoint = Configuration.getGlobalConfiguration().get("AZURE_AI_ENDPOINT", "your-ai-endpoint");
        deploymentName = Configuration.getGlobalConfiguration().get("DEPLOYMENT_NAME", "your-deployment-name");
        apiKey = Configuration.getGlobalConfiguration().get("AZURE_API_KEY", "your-api-key");
        
        deploymentsClient = new AIProjectClientBuilder()
            .endpoint(endpoint)
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildDeploymentsClient();
    }
    
    @Test
    public void testDeploymentExists() {
        // Skip test if deployment name is not provided
        if (deploymentName == null || deploymentName.isEmpty()) {
            System.out.println("Skipping test: DEPLOYMENT_NAME environment variable not set");
            return;
        }
        
        // Verify deployment exists
        assertDoesNotThrow(() -> deploymentsClient.get(deploymentName));
    }
    
    @Test
    public void testLangChainIntegration() {
        // Skip test if required environment variables are not set
        if (endpoint == null || endpoint.isEmpty() || 
            apiKey == null || apiKey.isEmpty() ||
            deploymentName == null || deploymentName.isEmpty()) {
            System.out.println("Skipping test: Required environment variables not set");
            return;
        }
        
        // Create the LangChain4j model
        AzureOpenAiChatModel model = AzureOpenAiChatModel.builder()
            .endpoint(endpoint)
            .apiKey(apiKey)
            .deploymentName(deploymentName)
            .build();
            
        // Create a simple assistant
        SimpleAssistant assistant = AiServices.create(SimpleAssistant.class, model);
        
        // Test the assistant
        String response = assistant.chat("Hello");
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}
