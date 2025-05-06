package com.example.ai.projects;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.IndexesClient;
import com.azure.ai.projects.models.AzureAISearchIndex;
import com.azure.ai.projects.models.Index;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

/**
 * Sample showing how to create a search index using the Azure SDK for Java.
 */
public class IndexCreateSample {
    /**
     * Main method to demonstrate how to create a search index with Azure SDK for Java.
     * @param args Command line arguments (not used).
     */    public static void main(String[] args) {
        // Create a client using DefaultAzureCredential
        IndexesClient indexesClient = new AIProjectClientBuilder()
            .endpoint(Configuration.getGlobalConfiguration().get("AZURE_AI_ENDPOINT", "your-ai-endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildIndexesClient();

        // Get configuration values
        String indexName = Configuration.getGlobalConfiguration().get("INDEX_NAME", "my-search-index");
        String indexVersion = Configuration.getGlobalConfiguration().get("INDEX_VERSION", "1.0");
        String aiSearchConnectionName = Configuration.getGlobalConfiguration().get("AI_SEARCH_CONNECTION_NAME", "my-ai-search-connection");
        String aiSearchIndexName = Configuration.getGlobalConfiguration().get("AI_SEARCH_INDEX_NAME", "my-ai-search-index-name");
        
        // Create or update an Azure AI Search index
        Index index = indexesClient.createOrUpdateVersion(
            indexName,
            indexVersion,
            new AzureAISearchIndex(aiSearchConnectionName, aiSearchIndexName)
        );
        
        System.out.println("Index created: " + index.getId());
        System.out.println("Index name: " + index.getName());
        System.out.println("Index version: " + index.getVersion());
    }
}
