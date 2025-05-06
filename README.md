# Azure SDK for Java Documentation

This repository contains comprehensive documentation and sample code for using the Azure SDK for Java with focus on AI capabilities.

## Documentation

- [Azure SDK for Java with Maven](azure-sdk-java-maven.md) - Basic usage with Maven
- [Azure SDK for Java with LangChain4j](azure-sdk-java-langchain4j.md) - Integration with LangChain4j framework
- [Azure SDK for Java with Spring AI](azure-sdk-java-spring-ai.md) - Integration with Spring AI framework

## Samples

The [samples](samples/) directory contains working examples for all integration approaches:

- [Maven Samples](samples/maven/) - Basic Java with Maven examples
- [LangChain4j Samples](samples/langchain4j/) - Examples of LangChain4j integration
- [Spring AI Samples](samples/spring-ai/) - Examples of Spring AI integration

See the [Setup Guide](samples/setup-guide.md) for information on how to configure and run the samples.

## Getting Started

To get started with Azure SDK for Java:

1. Install the SDK using Maven:
   ```xml
   <dependency>
       <groupId>com.azure</groupId>
       <artifactId>azure-ai-projects</artifactId>
       <version>1.0.0-alpha.20250429.2</version>
   </dependency>
   ```

2. Authenticate with Azure:
   ```java
   ConnectionsClient connectionsClient = new AIProjectClientBuilder()
       .endpoint("your-ai-endpoint")
       .credential(new DefaultAzureCredentialBuilder().build())
       .buildConnectionsClient();
   ```

3. Start using the SDK to interact with Azure AI services:
   ```java
   // List connections
   for (Connection connection : connectionsClient.list(null, null)) {
       System.out.println("Connection: " + connection.getName());
   }
   ```

## Features

The Azure SDK for Java provides access to:

- **Connections**: Manage connections to external services
- **Deployments**: Work with deployed models
- **Indexes**: Create and manage search indexes
- **Datasets**: Manage AI datasets
- **Evaluations**: Evaluate AI models
- **Red Teams**: Work with Red Team testing

## Requirements

- Java Development Kit (JDK) 17 or later
- Maven 3.6.0 or later
- An Azure subscription with access to Azure AI services
- Access to Azure AI Foundry

## Additional Resources

- [Azure AI Foundry Documentation](https://learn.microsoft.com/azure/ai-foundry/)
- [Azure SDK for Java](https://github.com/Azure/azure-sdk-for-java)
- [LangChain4j Documentation](https://github.com/langchain4j/langchain4j)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/index.html)
- [Azure OpenAI Service Documentation](https://learn.microsoft.com/azure/ai-services/openai/)
