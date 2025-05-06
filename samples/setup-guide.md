# Azure SDK for Java Samples Setup Guide

This guide will walk you through the steps to set up and run the sample code for the Azure SDK for Java.

## Prerequisites

### Required Components

- **Java Development Kit (JDK) 17 or later**
  - We recommend the [Microsoft Build of OpenJDK](https://learn.microsoft.com/en-us/java/openjdk/download), which is a free, Long-Term Support (LTS) distribution of OpenJDK
  - After installation, verify with: `java -version`

- **Maven 3.6.0 or later**
  - Download from the [Apache Maven website](https://maven.apache.org/download.cgi)
  - After installation, verify with: `mvn -version`

- **An Azure Account**
  - Free account: [Create an Azure account](https://azure.microsoft.com/free/)
  - Active subscription with access to Azure AI services
  
- **Azure AI Services**
  - Access to [Azure AI Foundry](https://ai.azure.com)
  - Appropriate permissions to create and manage AI resources

- **Azure CLI (optional but recommended)**
  - Install from [Azure CLI installation guide](https://docs.microsoft.com/cli/azure/install-azure-cli)
  - Use for Azure resource management and authentication
  - After installation, verify with: `az --version`

### Development Environment

#### Visual Studio Code Setup (Recommended)

1. **Install Visual Studio Code**
   - Download and install from [VS Code website](https://code.visualstudio.com/)

2. **Install Java Extensions**
   - Install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) which includes:
     - Language Support for Java by Red Hat
     - Debugger for Java
     - Test Runner for Java
     - Maven for Java
     - Project Manager for Java
     - Visual Studio IntelliCode
   
3. **Configure Java in VS Code**
   - Set JAVA_HOME environment variable pointing to your JDK installation
   - Follow the [VS Code Java setup guide](https://code.visualstudio.com/docs/languages/java) for detailed instructions

4. **Install Additional Helpful Extensions**
   - [Azure Tools](https://marketplace.visualstudio.com/items?itemName=ms-vscode.vscode-node-azure-pack) for Azure integration
   - [GitHub Pull Requests](https://marketplace.visualstudio.com/items?itemName=GitHub.vscode-pull-request-github) for GitHub integration

#### Other IDEs
You can also use other IDEs such as:
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Community or Ultimate edition)
- [Eclipse](https://www.eclipse.org/downloads/) with the Maven plugin

## Environment Setup

Before running the samples, you'll need to set up your Java environment and configure the necessary Azure credentials.

### Project Configuration

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Azure/azure-ai-foundry-model-java-docs.git
   cd azure-ai-foundry-model-java-docs
   ```

2. **Build with Maven**
   Maven will handle dependency management for the project. Each sample directory contains its own `pom.xml` file:
   ```bash
   cd samples/maven  # or samples/langchain4j or samples/spring-ai
   mvn clean install
   ```

3. **IDE Setup**
   - For VS Code: Open the folder and let the Java extension detect the project
   - For IntelliJ IDEA: Use "Open" and select the pom.xml file to import as a Maven project
   - For Eclipse: Import as "Existing Maven Project"

### Environment Variables

To run the samples, you need to set up environment variables that contain your Azure AI credentials and configuration values.

#### Using .env Files (Recommended)

For local development, you can use a `.env` file to manage your environment variables. This approach makes it easier to switch between different configurations.

1. Copy the `.env.template` file from the repository root to a new file named `.env`:
   ```bash
   cp .env.template .env
   ```

2. Edit the `.env` file and add your specific values:
   ```
   AZURE_AI_ENDPOINT=https://your-resource.azure.com
   CONNECTION_NAME=your-connection-name
   DEPLOYMENT_NAME=your-deployment-name
   INDEX_NAME=your-index-name
   INDEX_VERSION=your-index-version
   AI_SEARCH_CONNECTION_NAME=your-ai-search-connection-name
   AI_SEARCH_INDEX_NAME=your-ai-search-index-name
   ```

3. If you're using VS Code, install the "DotENV" extension to get syntax highlighting for your `.env` files.

4. The `.env` file is listed in `.gitignore` to prevent accidentally committing your credentials to version control.

> **Note**: The samples are configured to automatically load environment variables from a `.env` file using the dotenv-java library.

#### Loading Environment Variables in Code

The samples use the `dotenv-java` library to load environment variables from a `.env` file. Here's an example of how to implement this in your code:

```java
import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentExample {
    public static void main(String[] args) {
        // Load environment variables from .env file (if present)
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing() // Don't throw exception if .env file is not found
            .load();

        // Access environment variables with fallback to system environment variables
        String endpoint = dotenv.get("AZURE_AI_ENDPOINT", System.getenv("AZURE_AI_ENDPOINT"));
        String connectionName = dotenv.get("CONNECTION_NAME", System.getenv("CONNECTION_NAME"));
        
        // Use variables in application
        System.out.println("Using endpoint: " + endpoint);
        System.out.println("Using connection: " + connectionName);
    }
}
```

This approach allows your application to read from either:
- The `.env` file in the project root
- System environment variables (if .env is not present or variable isn't in .env)

### Creating an Environment Configuration Utility

For larger projects, consider creating a dedicated utility class to manage environment variables:

```java
package com.example.util;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Utility class for handling environment variables.
 */
public class EnvironmentConfig {
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    /**
     * Get an environment variable with a fallback value.
     */
    public static String get(String key, String defaultValue) {
        String value = dotenv.get(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return value != null ? value : defaultValue;
    }

    /**
     * Get an environment variable (required).
     * Throws an IllegalArgumentException if the variable is not set.
     */
    public static String getRequired(String key) {
        String value = get(key, null);
        if (value == null) {
            throw new IllegalArgumentException(
                    "Required environment variable '" + key + "' is not set.");
        }
        return value;
    }

    // Convenience methods for common environment variables
    public static String getAzureAIEndpoint() {
        return getRequired("AZURE_AI_ENDPOINT");
    }

    public static String getConnectionName() {
        return getRequired("CONNECTION_NAME");
    }
}
```

Usage example:

```java
import com.example.util.EnvironmentConfig;

// Get required environment variables (throws exception if not set)
String endpoint = EnvironmentConfig.getAzureAIEndpoint();
String connectionName = EnvironmentConfig.getConnectionName();

// Get optional environment variables with default values
String timeout = EnvironmentConfig.get("REQUEST_TIMEOUT_SECONDS", "30");
```

This utility class is included in the samples under `com.example.util.EnvironmentConfig`.

#### Setting Environment Variables Manually

You can also set environment variables directly in your operating system.

##### Windows Command Prompt
```cmd
set AZURE_AI_ENDPOINT=your-ai-endpoint-url
set CONNECTION_NAME=your-connection-name
set DEPLOYMENT_NAME=your-deployment-name
set INDEX_NAME=your-index-name
set INDEX_VERSION=your-index-version
set AI_SEARCH_CONNECTION_NAME=your-ai-search-connection-name
set AI_SEARCH_INDEX_NAME=your-ai-search-index-name
```

#### Windows PowerShell
```powershell
$env:AZURE_AI_ENDPOINT = "your-ai-endpoint"
$env:CONNECTION_NAME = "your-connection-name"
$env:DEPLOYMENT_NAME = "your-deployment-name"
$env:INDEX_NAME = "your-index-name"
$env:INDEX_VERSION = "your-index-version"
$env:AI_SEARCH_CONNECTION_NAME = "your-ai-search-connection-name"
$env:AI_SEARCH_INDEX_NAME = "your-ai-search-index-name"
```

#### WSL/Linux/macOS
```bash
export AZURE_AI_ENDPOINT=your-ai-endpoint
export CONNECTION_NAME=your-connection-name
export DEPLOYMENT_NAME=your-deployment-name
export INDEX_NAME=your-index-name
export INDEX_VERSION=your-index-version
export AI_SEARCH_CONNECTION_NAME=your-ai-search-connection-name
export AI_SEARCH_INDEX_NAME=your-ai-search-index-name
```

## Building the Samples

### Maven Configuration

Each sample project contains a `pom.xml` file that defines the project dependencies. The key dependencies are:

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
</dependencies>
```

> **Note**: The artifactId `azure-ai-projects` is maintained for backwards compatibility. As the Azure SDK for Java continues to evolve, this package name may be updated in future releases.

### Building the Project

Navigate to the sample directory and build using Maven:

```bash
cd samples/maven  # or samples/langchain4j or samples/spring-ai
mvn clean package
```

When you run this command, Maven will:
1. Download all necessary dependencies
2. Compile the Java source code
3. Run any unit tests
4. Package the application into a JAR file

## Running the Samples

After building, you can run the samples using the Maven exec plugin:

### Basic Connection Sample
```bash
mvn exec:java -Dexec.mainClass="com.example.ai.projects.BasicConnectionSample"
```

### List Connections Sample
```bash
mvn exec:java -Dexec.mainClass="com.example.ai.projects.ListConnectionsSample"
```

### Deployments Sample
```bash
mvn exec:java -Dexec.mainClass="com.example.ai.projects.DeploymentsSample"
```

### Index Create Sample
```bash
mvn exec:java -Dexec.mainClass="com.example.ai.projects.IndexCreateSample"
```

## Running the Tests

Run the tests using the following command:

```bash
mvn test
```

## Security Best Practices

When working with Azure AI services and credentials:

### Local Development

1. **Environment Variables**
   - Use `.env` files for local development
   - Keep `.env` files out of version control (listed in `.gitignore`)
   - Use the dotenv-java library to load variables from `.env` files

2. **Authentication**
   - Use Azure Identity library (`DefaultAzureCredential`) for authentication
   - Implement principle of least privilege for your service accounts
   - Rotate keys and secrets regularly

### Production Environments

For production environments, you should use more secure methods for handling credentials:

1. **Azure Key Vault**

   ```java
   // Add dependency in pom.xml
   // <dependency>
   //     <groupId>com.azure</groupId>
   //     <artifactId>azure-security-keyvault-secrets</artifactId>
   //     <version>4.7.1</version>
   // </dependency>
   
   // Create a client to access Key Vault
   SecretClient secretClient = new SecretClientBuilder()
       .vaultUrl("https://your-vault.vault.azure.net/")
       .credential(new DefaultAzureCredentialBuilder().build())
       .buildClient();
   
   // Get a secret
   String endpoint = secretClient.getSecret("azure-ai-endpoint").getValue();
   ```

   The sample code includes a full example in `com.example.security.KeyVaultSecurityExample`.

2. **Managed Identities**

   For Azure-hosted applications (App Service, Functions, VMs, etc.), use managed identities:

   ```java
   // Use DefaultAzureCredential which can use managed identities when available
   AIProjectClientBuilder builder = new AIProjectClientBuilder()
       .endpoint(endpoint)
       .credential(new DefaultAzureCredentialBuilder().build());
   ```

3. **CI/CD Pipelines**
   - Use Azure Pipeline variables or GitHub Secrets
   - Never hardcode credentials in your pipeline configuration
   - Consider using service principals with limited scope

4. **Data Handling**
   - Be mindful of what data you send to AI services and store from responses
   - Implement data minimization practices
   - Consider privacy and compliance requirements for your region

5. **Network Security**
   - Use private endpoints where possible
   - Restrict IP ranges that can access your Azure resources
   - Enable Azure Private Link for sensitive resources

6. **Dependency Management**
   - Keep your dependencies up to date to avoid security vulnerabilities
   - Use Maven's dependency management to track and update dependencies
   - Consider using tools like Dependabot or Snyk for vulnerability scanning

## Troubleshooting

- **Authentication Errors**: Make sure you are properly authenticated with Azure. You can use the `az login` command in the Azure CLI to log in.
- **Missing Environment Variables**: Check that all required environment variables are set.
- **Endpoint Access**: Ensure your Azure account has access to the specified Azure AI endpoint.
- **Maven Repository Access**: Make sure you have access to the Azure SDK for Java Maven repository.
- **JDK Compatibility**: Ensure you're using JDK 17 or later as required by the samples.
- **Maven Build Issues**: Try clearing your local Maven repository cache if you encounter strange build issues:
  ```bash
  rm -rf ~/.m2/repository/com/azure/ai-*
  ```

## Getting Help

If you need help with the Azure SDK for Java, you can:

- Reference the [Azure AI documentation](https://learn.microsoft.com/azure/ai-foundry/)
- Visit the [Azure SDK for Java GitHub repository](https://github.com/Azure/azure-sdk-for-java)
- Contact [Azure Support](https://azure.microsoft.com/support/)

## Project Structure

The repository is organized into these main directories:

```
azure-ai-foundry-model-java-docs/
├── README.md                       # Main documentation
├── azure-sdk-java-maven.md         # Maven integration guide
├── azure-sdk-java-langchain4j.md   # LangChain4j integration guide
├── azure-sdk-java-spring-ai.md     # Spring AI integration guide
└── samples/                        # Code samples
    ├── maven/                      # Basic Maven samples
    │   ├── src/main/java           # Sample code
    │   └── src/test/java           # Test code
    ├── langchain4j/                # LangChain4j integration samples
    │   ├── src/main/java           # Sample code
    │   └── src/test/java           # Test code
    ├── spring-ai/                  # Spring AI integration samples
    │   ├── src/main/java           # Sample code
    │   └── src/test/java           # Test code
    └── setup-guide.md              # This setup guide
```

## README Information

Each sample directory includes:

1. **Sample Code**: Java files demonstrating specific functionality
2. **Tests**: Java tests to verify the functionality
3. **Dependencies**: Maven pom.xml with required dependencies

The samples demonstrate:
- Basic connection and authentication with Azure AI services
- Working with AI deployments and models
- Creating and managing indexes for search
- Integration with popular frameworks like LangChain4j and Spring AI

When adapting these samples for your own projects, be sure to:
- Update the environment variables with your own Azure resource details
- Follow best practices for securing credentials in production environments
- Check for updates to the Azure SDK for Java for new features and improvements
