# Azure SDK for Java with Maven

Get Started with Azure SDK for Java using Maven. Below are example code snippets for a few use cases. For additional information about Azure SDK for Java, see the [full documentation](https://learn.microsoft.com/azure/ai-foundry/) and [samples](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects).

## 1. Authentication using Azure Identity

For Azure AI endpoints, you need to authenticate against the service. Azure SDK for Java supports several authentication mechanisms, with Azure Identity being the recommended approach for application authentication.

To create a client with the Azure SDK for Java using Azure Identity, initialize the client by passing your Azure credentials to the SDK's configuration:

```java
import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.ConnectionsClient;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.core.util.Configuration;

// Create a client using DefaultAzureCredential
ConnectionsClient connectionsClient = new AIProjectClientBuilder()
    .endpoint(Configuration.getGlobalConfiguration().get("AZURE_AI_ENDPOINT", "your-ai-endpoint"))
    .credential(new DefaultAzureCredentialBuilder().build())
    .buildConnectionsClient();
```

## 2. Install Dependencies

To install the Azure SDK for Java, add this in your Maven pom.xml:

```xml
<dependencies>
    <dependency>
        <groupId>com.azure</groupId>
        <artifactId>azure-ai-projects</artifactId>
        <version>1.0.0-alpha.20250429.2</version>
    </dependency>
    <dependency>
        <groupId>com.azure</groupId>
        <artifactId>azure-identity</artifactId>
        <version>1.15.4</version>
    </dependency>
    <dependency>
        <groupId>com.openai</groupId>
        <artifactId>openai-java</artifactId>
        <version>1.4.1</version>
    </dependency>
</dependencies>
```

You can connect to the Azure Maven feed using the following link: https://dev.azure.com/azure-sdk/public/_artifacts/feed/azure-sdk-for-java/connect

For each of the code snippets below, copy the content into a sample.java file and run as a package, for instance:
```bash
mvn clean package
mvn exec:java -Dexec.mainClass="com.example.ai.projects.BasicConnectionSample"
```

## 3. Run Basic Code Samples

### 3.1 Getting Connections

This sample demonstrates how to get a connection using the Azure SDK for Java:

```java
package com.example.ai.projects;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.ConnectionsClient;
import com.azure.ai.projects.models.Connection;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

import java.util.Map;

public class BasicConnectionSample {
    /**
     * Main method to demonstrate how to get a connection using the Azure SDK for Java.
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
```

### 3.2 Listing Connections

This sample demonstrates how to list all connections available in your Azure workspace:

```java
package com.example.ai.projects;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.ConnectionsClient;
import com.azure.ai.projects.models.Connection;
import com.azure.ai.projects.models.ListViewType;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

public class ListConnectionsSample {
    /**
     * Main method to demonstrate how to list connections using the Azure SDK for Java.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Create a client using DefaultAzureCredential
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
```

### 3.3 Working with Deployments

This sample demonstrates how to get deployment information:

```java
package com.example.ai.projects;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.ai.projects.models.Deployment;
import com.azure.ai.projects.models.ListViewType;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

public class DeploymentsSample {
    /**
     * Main method to demonstrate how to work with deployments using the Azure SDK for Java.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Create a client using DefaultAzureCredential
        DeploymentsClient deploymentsClient = new AIProjectClientBuilder()
            .endpoint(Configuration.getGlobalConfiguration().get("AZURE_AI_ENDPOINT", "your-ai-endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildDeploymentsClient();

        // Get a specific deployment
        String deploymentName = Configuration.getGlobalConfiguration().get("DEPLOYMENT_NAME", "your-deployment-name");
        Deployment deployment = deploymentsClient.get(deploymentName);
        System.out.printf("Deployment name: %s%n", deployment.getName());
        System.out.printf("Deployment type: %s%n", deployment.getType().getValue());

        // List all deployments
        System.out.println("\nListing all deployments:");
        for (Deployment dep : deploymentsClient.list(null, ListViewType.ALL)) {
            System.out.printf("Deployment name: %s, type: %s%n", 
                dep.getName(),
                dep.getType().getValue());
        }
    }
}
```

### 3.4 Creating a Search Index

This sample demonstrates how to create or update a search index:

```java
package com.example.ai.projects;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.IndexesClient;
import com.azure.ai.projects.models.AzureAISearchIndex;
import com.azure.ai.projects.models.Index;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

public class IndexCreateSample {
    /**
     * Main method to demonstrate how to create a search index using the Azure SDK for Java.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
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
```

## 4. Setting Up Environment Variables

For the code samples above, you'll need to set up environment variables in your development environment. Here are the key environment variables used:

```
AZURE_AI_ENDPOINT=your-ai-endpoint
CONNECTION_NAME=your-connection-name
DEPLOYMENT_NAME=your-deployment-name
INDEX_NAME=your-index-name
INDEX_VERSION=your-index-version
AI_SEARCH_CONNECTION_NAME=your-ai-search-connection-name
AI_SEARCH_INDEX_NAME=your-ai-search-index-name
```

You can set these environment variables using:

- **Windows Command Prompt**: `set AZURE_AI_ENDPOINT=your-ai-endpoint`
- **Windows PowerShell**: `$env:AZURE_AI_ENDPOINT = "your-ai-endpoint"`
- **macOS/Linux**: `export AZURE_AI_ENDPOINT=your-ai-endpoint`

## 5. Testing the Samples

To verify that the samples are working correctly, create a simple test class:

```java
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
```

Add JUnit dependency to your pom.xml:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.9.3</version>
    <scope>test</scope>
</dependency>
```

## Additional Resources

- [Azure AI Foundry Documentation](https://learn.microsoft.com/azure/ai-foundry/)
- [Azure SDK for Java](https://github.com/Azure/azure-sdk-for-java)
- [Azure Identity Documentation](https://docs.microsoft.com/java/api/overview/azure/identity-readme)
