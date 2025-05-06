# Azure SDK for Java with Spring AI

This guide shows how to integrate Azure SDK for Java with Spring AI to create powerful AI applications within a Spring Boot environment.

## 1. Installation

Add the following dependencies to your Maven pom.xml:

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.2.3</version>
    </dependency>
    
    <!-- Spring AI Core -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-core</artifactId>
        <version>0.8.1</version>
    </dependency>
    
    <!-- Spring AI OpenAI Integration -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-azure-openai-spring-boot-starter</artifactId>
        <version>0.8.1</version>
    </dependency>
      <!-- Azure SDK for Java -->
    <dependency>
        <groupId>com.azure</groupId>
        <artifactId>azure-ai-projects</artifactId>
        <version>1.0.0-alpha.20250429.2</version>
    </dependency>
    
    <!-- Azure Identity for authentication -->
    <dependency>
        <groupId>com.azure</groupId>
        <artifactId>azure-identity</artifactId>
        <version>1.15.4</version>
    </dependency>
    
    <!-- OpenAI Java SDK -->
    <dependency>
        <groupId>com.openai</groupId>
        <artifactId>openai-java</artifactId>
        <version>1.4.1</version>
    </dependency>
    
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <version>3.2.3</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 2. Configuring the Spring Application

### 2.1 application.properties/yml Configuration

Create an `application.yml` file in your project's `src/main/resources` directory:

```yaml
spring:
  ai:
    azure:
      openai:
        api-key: ${AZURE_API_KEY}
        endpoint: ${AZURE_OPENAI_ENDPOINT}
        chat:
          options:
            model: ${AZURE_OPENAI_DEPLOYMENT_NAME}
            temperature: 0.7
            max-tokens: 2000
            
azure:
  ai:
    projects:
      endpoint: ${AZURE_AI_PROJECTS_ENDPOINT}
```

## 3. Integrating Azure SDK for Java with Spring AI

### 3.1 Main Application Class

Create a Spring Boot application class:

```java
package com.example.ai.projects.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AzureAISpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(AzureAISpringApplication.class, args);
    }
}
```

### 3.2 Configuration Class for Azure SDK for Java

Set up a configuration class to create beans for Azure SDK for Java:

```java
package com.example.ai.projects.spring.config;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.ConnectionsClient;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.ai.projects.IndexesClient;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureAIProjectsConfig {
    
    @Value("${azure.ai.projects.endpoint}")
    private String aiProjectsEndpoint;
    
    @Bean
    public AIProjectClientBuilder aiProjectClientBuilder() {
        return new AIProjectClientBuilder()
            .endpoint(aiProjectsEndpoint)
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
```

### 3.3 Service Class for Azure SDK for Java

Create a service class to handle Azure SDK for Java operations:

```java
package com.example.ai.projects.spring.service;

import com.azure.ai.projects.ConnectionsClient;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.ai.projects.models.Connection;
import com.azure.ai.projects.models.Deployment;
import com.azure.ai.projects.models.ListViewType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AzureAIProjectsService {
    
    private final ConnectionsClient connectionsClient;
    private final DeploymentsClient deploymentsClient;
    
    public AzureAIProjectsService(ConnectionsClient connectionsClient, DeploymentsClient deploymentsClient) {
        this.connectionsClient = connectionsClient;
        this.deploymentsClient = deploymentsClient;
    }
    
    public List<Connection> listConnections() {
        List<Connection> connections = new ArrayList<>();
        connectionsClient.list(null, ListViewType.ALL).forEach(connections::add);
        return connections;
    }
    
    public Connection getConnection(String connectionName) {
        return connectionsClient.get(connectionName);
    }
    
    public List<Deployment> listDeployments() {
        List<Deployment> deployments = new ArrayList<>();
        deploymentsClient.list(null, ListViewType.ALL).forEach(deployments::add);
        return deployments;
    }
    
    public Deployment getDeployment(String deploymentName) {
        return deploymentsClient.get(deploymentName);
    }
}
```

### 3.4 Create a REST Controller to Access AI Projects

Create a controller to expose Azure SDK for Java information via REST endpoints:

```java
package com.example.ai.projects.spring.controller;

import com.azure.ai.projects.models.Connection;
import com.azure.ai.projects.models.Deployment;
import com.example.ai.projects.spring.service.AzureAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AzureAIController {
    
    private final AzureAIService aiService;
    
    public AzureAIController(AzureAIService aiService) {
        this.aiService = aiService;
    }
    
    @GetMapping("/connections")
    public ResponseEntity<List<Connection>> listConnections() {
        return ResponseEntity.ok(aiProjectsService.listConnections());
    }
    
    @GetMapping("/connections/{name}")
    public ResponseEntity<Connection> getConnection(@PathVariable String name) {
        return ResponseEntity.ok(aiProjectsService.getConnection(name));
    }
    
    @GetMapping("/deployments")
    public ResponseEntity<List<Deployment>> listDeployments() {
        return ResponseEntity.ok(aiProjectsService.listDeployments());
    }
    
    @GetMapping("/deployments/{name}")
    public ResponseEntity<Deployment> getDeployment(@PathVariable String name) {
        return ResponseEntity.ok(aiProjectsService.getDeployment(name));
    }
}
```

## 4. Integrating Spring AI with Azure AI Projects

### 4.1 Creating an AI Chat Service

Create a service that combines Spring AI with Azure AI Projects:

```java
package com.example.ai.projects.spring.service;

import com.azure.ai.projects.models.Deployment;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIChatService {
      private final ChatClient chatClient;
    private final AzureAIService aiService;
    
    public AIChatService(ChatClient chatClient, AzureAIService aiService) {
        this.chatClient = chatClient;
        this.aiService = aiService;
    }
    
    public String chat(String userMessage) {
        return chatClient.call(userMessage);
    }
    
    public String chatAboutDeployment(String deploymentName) {
        try {            // Get deployment info using Azure SDK for Java
            Deployment deployment = aiProjectsService.getDeployment(deploymentName);
            
            // Create a system prompt with deployment information
            String systemPromptTemplate = "You are an AI assistant that provides information about Azure SDK for Java. " +
                "Here's information about a deployment named {{name}}:\n" +
                "- Type: {{type}}\n" +
                "- Created at: {{createdAt}}\n" +
                "Please answer user questions about this deployment.";
            
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", deployment.getName());
            variables.put("type", deployment.getType().getValue());
            variables.put("createdAt", deployment.getCreatedAt().toString());
            
            Message systemMessage = new SystemPromptTemplate(systemPromptTemplate).create(variables);
            Message userMessage = new UserMessage("Tell me about this deployment");
            
            List<Message> messages = new ArrayList<>();
            messages.add(systemMessage);
            messages.add(userMessage);
            
            Prompt prompt = new Prompt(messages);
            ChatResponse response = chatClient.call(prompt);
            
            return response.getResult().getOutput().getContent();
        } catch (Exception e) {
            return "Error getting information about deployment: " + e.getMessage();
        }
    }
}
```

### 4.2 Creating a Chat Controller

Create a controller for the chat interface:

```java
package com.example.ai.projects.spring.controller;

import com.example.ai.projects.spring.service.AIChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    private final AIChatService chatService;
    
    public ChatController(AIChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping
    public ResponseEntity<String> chat(@RequestBody String message) {
        return ResponseEntity.ok(chatService.chat(message));
    }
    
    @GetMapping("/deployments/{name}")
    public ResponseEntity<String> chatAboutDeployment(@PathVariable String name) {
        return ResponseEntity.ok(chatService.chatAboutDeployment(name));
    }
}
```

## 5. Creating a RAG Application with Spring AI and Azure SDK for Java

### 5.1 Setting up a Document Repository

Create a service for document embedding and retrieval:

```java
package com.example.ai.projects.spring.service;

import com.azure.ai.projects.IndexesClient;
import com.azure.ai.projects.models.AzureAISearchIndex;
import com.azure.ai.projects.models.Index;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {
    
    private final SimpleVectorStore vectorStore;
    private final EmbeddingClient embeddingClient;
    private final IndexesClient indexesClient;
    
    public DocumentService(SimpleVectorStore vectorStore, 
                          EmbeddingClient embeddingClient,
                          IndexesClient indexesClient) {
        this.vectorStore = vectorStore;
        this.embeddingClient = embeddingClient;
        this.indexesClient = indexesClient;
    }
    
    public void addDocument(String content, String metadata) {
        Document document = new Document(content, metadata);
        vectorStore.add(List.of(document));
    }
    
    public List<Document> search(String query, int maxResults) {
        return vectorStore.similaritySearch(
            SearchRequest.query(query).withTopK(maxResults)
        );
    }
    
    public Index createOrUpdateAzureSearchIndex(String indexName, 
                                              String indexVersion,
                                              String connectionName,
                                              String searchIndexName) {
        return indexesClient.createOrUpdateVersion(
            indexName,
            indexVersion,
            new AzureAISearchIndex(connectionName, searchIndexName)
        );
    }
}
```

### 5.2 Creating a RAG Chat Service

Create a service that combines RAG with Spring AI:

```java
package com.example.ai.projects.spring.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagChatService {
    
    private final ChatClient chatClient;
    private final DocumentService documentService;
    
    public RagChatService(ChatClient chatClient, DocumentService documentService) {
        this.chatClient = chatClient;
        this.documentService = documentService;
    }
    
    public String ragChat(String userQuery) {
        // Step 1: Search for relevant documents
        List<Document> relevantDocs = documentService.search(userQuery, 3);
        
        // Step 2: Format document content for the prompt
        String context = relevantDocs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n\n"));
        
        // Step 3: Create a prompt with the context
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage("You are an AI assistant that answers questions based on the following context:\n" + context));
        messages.add(new UserMessage(userQuery));
        
        Prompt prompt = new Prompt(messages);
        
        // Step 4: Generate a response
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

### 5.3 Creating a RAG Controller

Create a controller for the RAG interface:

```java
package com.example.ai.projects.spring.controller;

import com.example.ai.projects.spring.service.DocumentService;
import com.example.ai.projects.spring.service.RagChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    
    private final RagChatService ragChatService;
    private final DocumentService documentService;
    
    public RagController(RagChatService ragChatService, DocumentService documentService) {
        this.ragChatService = ragChatService;
        this.documentService = documentService;
    }
    
    @PostMapping("/chat")
    public ResponseEntity<String> ragChat(@RequestBody String query) {
        return ResponseEntity.ok(ragChatService.ragChat(query));
    }
    
    @PostMapping("/documents")
    public ResponseEntity<String> addDocument(@RequestParam String content, 
                                            @RequestParam(required = false) String metadata) {
        documentService.addDocument(content, metadata != null ? metadata : "{}");
        return ResponseEntity.ok("Document added successfully");
    }
}
```

## 6. Sample Unit Tests

Create a test class to verify Spring AI and Azure SDK for Java integration:

```java
package com.example.ai.projects.spring.test;

import com.azure.ai.projects.ConnectionsClient;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.ai.projects.models.Connection;
import com.azure.ai.projects.models.Deployment;
import com.example.ai.projects.spring.service.AIChatService;
import com.example.ai.projects.spring.service.AzureAIProjectsService;
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
        List<Connection> connections = aiProjectsService.listConnections();
        
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
```

## 7. Running the Spring Boot Application

To run the Spring Boot application, use the following command:

```bash
mvn spring-boot:run
```

Ensure you have set the necessary environment variables:

```
AZURE_API_KEY=your-azure-api-key
AZURE_OPENAI_ENDPOINT=your-azure-openai-endpoint
AZURE_OPENAI_DEPLOYMENT_NAME=your-deployment-name
AZURE_AI_PROJECTS_ENDPOINT=your-ai-projects-endpoint
```

## Additional Resources

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/index.html)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Azure AI Studio Documentation](https://learn.microsoft.com/azure/ai-studio/)
- [Azure OpenAI Service Documentation](https://learn.microsoft.com/azure/ai-services/openai/)
