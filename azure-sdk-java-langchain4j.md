# Azure SDK for Java with LangChain4j

This guide shows how to integrate Azure SDK for Java with LangChain4j to create powerful AI applications. LangChain4j is a popular framework for building applications with large language models.

## 1. Installation

Add the following dependencies to your Maven pom.xml:

```xml
<dependencies>
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
    
    <!-- LangChain4j Core -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j</artifactId>
        <version>0.27.1</version>
    </dependency>
    
    <!-- LangChain4j OpenAI Integration -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-azure-open-ai</artifactId>
        <version>0.27.1</version>
    </dependency>
</dependencies>
```

## 2. Connecting Azure AI Projects with LangChain4j

### 2.1 Create a Basic Integration

This sample demonstrates how to integrate Azure AI Projects with LangChain4j for building an AI assistant:

```java
package com.example.ai.projects.langchain;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.ai.projects.models.Deployment;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;

public class LangChainIntegrationSample {

    // Define an assistant interface
    interface Assistant {
        @UserMessage("{{message}}")
        String chat(String message);
    }

    /**
     * Main method to demonstrate Azure SDK for Java integration with LangChain4j.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Get environment variables
        String endpoint = Configuration.getGlobalConfiguration().get("ENDPOINT", "your-endpoint-url");
        String deploymentName = Configuration.getGlobalConfiguration().get("DEPLOYMENT_NAME", "your-deployment-name");
        String apiKey = Configuration.getGlobalConfiguration().get("AZURE_API_KEY", "your-api-key");
        
        // First, use Azure SDK for Java to get deployment information
        DeploymentsClient deploymentsClient = new AIProjectClientBuilder()
            .endpoint(endpoint)
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildDeploymentsClient();
        
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
    }
}
```

### 2.2 RAG (Retrieval-Augmented Generation) with Azure AI Search

This sample demonstrates how to use Azure AI Search with LangChain4j for RAG applications:

```java
package com.example.ai.projects.langchain;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.IndexesClient;
import com.azure.ai.projects.models.AzureAISearchIndex;
import com.azure.ai.projects.models.Index;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.azure.AzureAiSearchEmbeddingStore;

import java.util.List;

public class RAGSearchSample {

    // Define a RAG-powered assistant interface
    interface RagAssistant {
        String answer(String question);
    }

    /**
     * Main method to demonstrate Azure AI Search RAG with LangChain4j.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Get environment variables
        String endpoint = Configuration.getGlobalConfiguration().get("ENDPOINT", "your-endpoint-url");
        String apiKey = Configuration.getGlobalConfiguration().get("AZURE_API_KEY", "your-api-key");
        String chatModelDeployment = Configuration.getGlobalConfiguration().get("CHAT_MODEL_DEPLOYMENT", "gpt-4o");
        String embeddingModelDeployment = Configuration.getGlobalConfiguration().get("EMBEDDING_MODEL_DEPLOYMENT", "text-embedding-3-small");
        String indexName = Configuration.getGlobalConfiguration().get("INDEX_NAME", "my-search-index");
        String aiSearchConnectionName = Configuration.getGlobalConfiguration().get("AI_SEARCH_CONNECTION_NAME", "my-search-connection");
        String aiSearchEndpoint = Configuration.getGlobalConfiguration().get("AI_SEARCH_ENDPOINT", "your-search-endpoint");
        String aiSearchKey = Configuration.getGlobalConfiguration().get("AI_SEARCH_KEY", "your-search-key");
        
        // Create or update the index using Azure SDK for Java
        IndexesClient indexesClient = new AIProjectClientBuilder()
            .endpoint(endpoint)
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildIndexesClient();
            
        Index index = indexesClient.createOrUpdateVersion(
            indexName,
            "1.0",
            new AzureAISearchIndex(aiSearchConnectionName, indexName)
        );
        
        System.out.println("Using index: " + index.getName());
        
        // Set up the embedding model using Azure OpenAI
        AzureOpenAiEmbeddingModel embeddingModel = AzureOpenAiEmbeddingModel.builder()
            .endpoint(endpoint)
            .apiKey(apiKey)
            .deploymentName(embeddingModelDeployment)
            .build();
            
        // Set up the chat model using Azure OpenAI    
        AzureOpenAiChatModel chatModel = AzureOpenAiChatModel.builder()
            .endpoint(endpoint)
            .apiKey(apiKey)
            .deploymentName(chatModelDeployment)
            .build();
            
        // Set up the Azure AI Search embedding store
        AzureAiSearchEmbeddingStore embeddingStore = AzureAiSearchEmbeddingStore.builder()
            .endpoint(aiSearchEndpoint)
            .apiKey(aiSearchKey)
            .indexName(indexName)
            .build();
            
        // Create example documents to index
        Document document = Document.from(
            "Azure SDK for Java is a service that helps you build, deploy, and manage AI solutions. " +
            "It includes tools for connecting to data sources, creating indexes, and deploying models."
        );
        
        // Split documents into chunks
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document).stream()
            .map(chunk -> TextSegment.from(chunk.text()))
            .toList();
            
        // Store embeddings in Azure AI Search
        embeddingStore.addAll(segments, embeddingModel);
        
        // Create a content retriever
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(3)
            .build();
            
        // Create a RAG-powered assistant
        RagAssistant assistant = AiServices.builder(RagAssistant.class)
            .chatLanguageModel(chatModel)
            .contentRetriever(contentRetriever)
            .build();
            
        // Ask a question
        String answer = assistant.answer("What is Azure SDK for Java?");
        System.out.println("Assistant answer: " + answer);
    }
}
```

### 2.3 Using Agents with Azure SDK for Java and LangChain4j

This sample demonstrates how to use tool-calling capabilities with LangChain4j:

```java
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

public class AgentToolUsageSample {

    // Define an agent interface with tool-calling capabilities
    interface AIProjectAgent {
        @SystemMessage("You are an AI assistant with expertise in Azure SDK for Java. " +
                     "Use the available tools to help answer questions.")
        @UserMessage("{{message}}")
        String chat(String message);
    }

    /**
     * Main method to demonstrate using LangChain4j with Azure SDK for Java for tool-calling.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Get environment variables
        String endpoint = Configuration.getGlobalConfiguration().get("ENDPOINT", "your-endpoint-url");
        String deploymentName = Configuration.getGlobalConfiguration().get("DEPLOYMENT_NAME", "your-deployment-name");
        String apiKey = Configuration.getGlobalConfiguration().get("AZURE_API_KEY", "your-api-key");
        
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
    }
    
    // A tools provider class that integrates with Azure SDK for Java
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
```

## 3. Testing Integration

This test class verifies that the LangChain4j integration works correctly:

```java
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
    private static DeploymentsClient deploymentsClient;
    private static String deploymentName;
    private static String endpoint;
    private static String apiKey;
    
    @BeforeAll
    public static void setup() {
        endpoint = Configuration.getGlobalConfiguration().get("ENDPOINT", "your-endpoint-url");
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
```

## Environment Setup

You'll need to set the following environment variables for the samples and tests to work correctly:

```
ENDPOINT=your-ai-projects-endpoint-url
DEPLOYMENT_NAME=your-chat-model-deployment-name
AZURE_API_KEY=your-azure-api-key
CHAT_MODEL_DEPLOYMENT=gpt-4o
EMBEDDING_MODEL_DEPLOYMENT=text-embedding-3-small
INDEX_NAME=my-search-index
AI_SEARCH_CONNECTION_NAME=my-search-connection
AI_SEARCH_ENDPOINT=your-search-endpoint
AI_SEARCH_KEY=your-search-key
```

## Additional Resources

- [Azure AI Foundry Documentation](https://learn.microsoft.com/azure/ai-foundry/)
- [LangChain4j Documentation](https://github.com/langchain4j/langchain4j)
- [Azure OpenAI Service Documentation](https://learn.microsoft.com/azure/ai-services/openai/)
- [Azure AI Search Documentation](https://learn.microsoft.com/azure/search/)
