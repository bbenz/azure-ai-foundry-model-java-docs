package com.example.ai.projects.test;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.ConnectionsClient;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConnectionsClientTest {
    private static ConnectionsClient connectionsClient;
    
    @BeforeAll
    public static void setup() {
        connectionsClient = new AIProjectClientBuilder()
            .endpoint(Configuration.getGlobalConfiguration().get("AZURE_AI_ENDPOINT", "your-ai-endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildConnectionsClient();
    }
    
    @Test
    public void testListConnections() {
        // Verify connection listing works
        assertDoesNotThrow(() -> connectionsClient.list(null, null).stream().count());
    }
    
    @Test
    public void testGetConnection() {
        String connectionName = Configuration.getGlobalConfiguration().get("CONNECTION_NAME");
        // Skip test if connection name is not provided
        if (connectionName == null || connectionName.isEmpty()) {
            System.out.println("Skipping test: CONNECTION_NAME environment variable not set");
            return;
        }
        
        // Verify get connection works
        assertDoesNotThrow(() -> connectionsClient.get(connectionName));
    }
}
