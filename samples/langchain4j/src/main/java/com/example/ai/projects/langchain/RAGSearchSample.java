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

/**
 * Sample demonstrating RAG with Azure AI Search and LangChain4j using Azure SDK for Java.
 */
public class RAGSearchSample {

    // Define a RAG-powered assistant interface
    interface RagAssistant {
        String answer(String question);
    }

    /**
     * Main method to demonstrate Azure AI Search RAG with Azure SDK for Java and LangChain4j.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Get environment variables
        String endpoint = Configuration.getGlobalConfiguration().get("AZURE_AI_ENDPOINT", "your-ai-endpoint");
        String apiKey = Configuration.getGlobalConfiguration().get("AZURE_API_KEY", "your-api-key");        String chatModelDeployment = Configuration.getGlobalConfiguration().get("CHAT_MODEL_DEPLOYMENT", "gpt-4o");
        String embeddingModelDeployment = Configuration.getGlobalConfiguration().get("EMBEDDING_MODEL_DEPLOYMENT", "text-embedding-3-small");
        String indexName = Configuration.getGlobalConfiguration().get("INDEX_NAME", "my-search-index");
        String aiSearchConnectionName = Configuration.getGlobalConfiguration().get("AI_SEARCH_CONNECTION_NAME", "my-search-connection");
        String aiSearchEndpoint = Configuration.getGlobalConfiguration().get("AI_SEARCH_ENDPOINT", "your-search-endpoint");
        String aiSearchKey = Configuration.getGlobalConfiguration().get("AI_SEARCH_KEY", "your-search-key");
        
        try {
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
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
