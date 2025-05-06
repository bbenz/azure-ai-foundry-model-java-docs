package com.example.ai.projects.spring.config;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.ConnectionsClient;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.ai.projects.IndexesClient;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Azure SDK for Java clients.
 */
@Configuration
public class AzureAIProjectsConfig {
    
    @Value("${azure.ai.endpoint}")
    private String aiEndpoint;
    
    @Bean
    public AIProjectClientBuilder aiProjectClientBuilder() {
        return new AIProjectClientBuilder()
            .endpoint(aiEndpoint)
            .credential(new DefaultAzureCredentialBuilder().build());
    }
    
    @Bean
    public ConnectionsClient connectionsClient(AIProjectClientBuilder builder) {
        return builder.buildConnectionsClient();
    }
    
    @Bean
    public DeploymentsClient deploymentsClient(AIProjectClientBuilder builder) {
        return builder.buildDeploymentsClient();
    }
    
    @Bean
    public IndexesClient indexesClient(AIProjectClientBuilder builder) {
        return builder.buildIndexesClient();
    }
}
