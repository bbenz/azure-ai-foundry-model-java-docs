package com.example.ai.projects;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.ConnectionsClient;
import com.azure.ai.projects.models.Connection;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

import java.util.Map;

/**
 * Sample showing how to get a connection using the Azure SDK for Java.
 */
public class BasicConnectionSample {
    /**
     * Main method to demonstrate how to get a connection using Azure SDK for Java.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Create a client using DefaultAzureCredential
        ConnectionsClient connectionsClient = new AIProjectClientBuilder()
            .endpoint(Configuration.getGlobalConfiguration().get("AZURE_AI_ENDPOINT", "your-ai-endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildConnectionsClient();

        // Get a connection by name
        String connectionName = Configuration.getGlobalConfiguration().get("CONNECTION_NAME", "your-connection-name");
        Connection connection = connectionsClient.get(connectionName);
        
        // Print connection details
        System.out.printf("Connection name: %s%n", connection.getName());
        System.out.printf("Connection type: %s%n", connection.getType().getValue());
        
        // Print connection metadata if available
        Map<String, String> metadata = connection.getMetadata();
        if (metadata != null) {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                System.out.printf("Metadata key: %s, value: %s%n", entry.getKey(), entry.getValue());
            }
        }
    }
}
