package com.example.ai.projects;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.ConnectionsClient;
import com.azure.ai.projects.models.Connection;
import com.azure.ai.projects.models.ListViewType;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

/**
 * Sample showing how to list connections using the Azure SDK for Java.
 */
public class ListConnectionsSample {
    /**
     * Main method to demonstrate how to list connections using Azure SDK for Java.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {        // Create a client using DefaultAzureCredential
        ConnectionsClient connectionsClient = new AIProjectClientBuilder()
            .endpoint(Configuration.getGlobalConfiguration().get("AZURE_AI_ENDPOINT", "your-ai-endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildConnectionsClient();

        // List all connections
        System.out.println("Listing all connections:");
        for (Connection connection : connectionsClient.list(null, ListViewType.ALL)) {
            System.out.printf("Connection name: %s, type: %s%n", 
                connection.getName(),
                connection.getType().getValue());
        }
    }
}
