package com.example.ai.projects.spring.test;

import com.azure.ai.projects.ConnectionsClient;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.ai.projects.models.Connection;
import com.azure.ai.projects.models.Deployment;
import com.example.ai.projects.spring.service.AIChatService;
import com.example.ai.projects.spring.service.AzureAIService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for Azure SDK for Java and Spring AI integration.
 */
@SpringBootTest
public class AzureAIIntegrationTest {
    
    @Autowired
    private AzureAIService aiService;
    
    @Autowired
    private AIChatService chatService;
    
    @MockBean
    private ConnectionsClient connectionsClient;
    
    @MockBean
    private DeploymentsClient deploymentsClient;
    
    @MockBean
    private ChatClient chatClient;
      @Test
    public void testListConnections() {
        // Arrange
        Connection mockConnection1 = createMockConnection("connection1");
        Connection mockConnection2 = createMockConnection("connection2");
        when(connectionsClient.list(any(), any())).thenReturn(Arrays.asList(mockConnection1, mockConnection2).iterator());
        
        // Act
        List<Connection> connections = aiService.listConnections();
        
        // Assert
        assertNotNull(connections);
        assertEquals(2, connections.size());
        assertEquals("connection1", connections.get(0).getName());
        assertEquals("connection2", connections.get(1).getName());
    }
    
    @Test
    public void testChatAboutDeployment() {
        // Arrange
        Deployment mockDeployment = createMockDeployment("test-deployment");
        when(deploymentsClient.get("test-deployment")).thenReturn(mockDeployment);
        when(chatClient.call(any(org.springframework.ai.chat.prompt.Prompt.class)))
            .thenReturn(new org.springframework.ai.chat.ChatResponse(
                new org.springframework.ai.chat.Generation("This is a test deployment")
            ));
        
        // Act
        String result = chatService.chatAboutDeployment("test-deployment");
        
        // Assert
        assertNotNull(result);
        assertEquals("This is a test deployment", result);
    }
    
    private Connection createMockConnection(String name) {
        Connection connection = new Connection();
        connection.setName(name);
        return connection;
    }
    
    private Deployment createMockDeployment(String name) {
        Deployment deployment = new Deployment();
        deployment.setName(name);
        deployment.setCreatedAt(OffsetDateTime.now());
        return deployment;
    }
}
