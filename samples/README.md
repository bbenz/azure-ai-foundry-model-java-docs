# Azure SDK for Java Samples

This repository contains sample code for the Azure SDK for Java with focus on AI capabilities.

## Getting Started

See the [Setup Guide](setup-guide.md) for information on how to set up your environment and run the samples.

## Prerequisites

- Java Development Kit (JDK) 17 or later
- Maven 3.6.0 or later
- An Azure subscription with access to Azure AI services
- Access to Azure AI Foundry

## Samples

This repository contains three sample projects:

1. **Maven**: Basic Java with Maven samples for using Azure SDK for Java directly.
2. **LangChain4j**: Samples showing integration with LangChain4j framework.
3. **Spring AI**: Samples showing integration with Spring AI framework.

## Building and Running the Samples

Each sample project can be built and run with Maven. See the individual project directories for specific instructions.

## Environment Variables

These sample projects require several environment variables to be set:

```
AZURE_AI_ENDPOINT=your-ai-endpoint-url
CONNECTION_NAME=your-connection-name
DEPLOYMENT_NAME=your-deployment-name
AZURE_OPENAI_ENDPOINT=your-azure-openai-endpoint
AZURE_OPENAI_DEPLOYMENT_NAME=your-azure-openai-deployment-name
AZURE_API_KEY=your-azure-api-key
INDEX_NAME=your-index-name
INDEX_VERSION=your-index-version
AI_SEARCH_CONNECTION_NAME=your-ai-search-connection-name
AI_SEARCH_INDEX_NAME=your-ai-search-index-name
AI_SEARCH_ENDPOINT=your-ai-search-endpoint
AI_SEARCH_KEY=your-ai-search-key
AZURE_AI_ENDPOINT=your-ai-endpoint
```

## Documentation

- [Azure SDK for Java with Maven](../azure-sdk-java-maven.md)
- [Azure SDK for Java with LangChain4j](../azure-sdk-java-langchain4j.md)
- [Azure SDK for Java with Spring AI](../azure-sdk-java-spring-ai.md)



## Additional Resources

- [Azure AI Foundry Documentation](https://learn.microsoft.com/azure/ai-foundry/)
- [Azure SDK for Java](https://github.com/Azure/azure-sdk-for-java)
- [LangChain4j Documentation](https://github.com/langchain4j/langchain4j)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/index.html)
