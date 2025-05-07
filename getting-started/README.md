# Getting Started with Azure AI Foundry

This directory contains samples and guides to help you get started with Azure AI Foundry using different programming languages.

## Available Samples

### Java Samples

The [Java folder](./java) contains samples demonstrating how to use the Azure SDK for Java to interact with Azure AI Foundry services, including:

- Basic client setup and authentication
- Chat completion with Azure OpenAI
- Agent creation and management
- File search and vector stores
- Agent evaluation
- Project and resource creation

## Prerequisites

- An Azure subscription with access to Azure AI services
- Access to Azure AI Foundry
- Language-specific prerequisites (see each language folder for details)

## Setting up Azure AI Foundry

Before running the samples, you need to:

1. Create an Azure AI Foundry resource in your Azure subscription
2. Create a project within your AI Foundry resource
3. Deploy a model (like GPT-4o) to use with your project
4. Configure your environment with the appropriate credentials and endpoints

See the [CreateProjectApp](./java/src/main/java/com/azure/ai/foundry/CreateProjectApp.java) sample for programmatic resource creation.

## Additional Resources

- [Azure AI Foundry Documentation](https://learn.microsoft.com/azure/ai-foundry/)
- [Azure SDK for Java](https://github.com/Azure/azure-sdk-for-java)
- [Azure AI Projects SDK for Java](https://central.sonatype.com/artifact/com.azure/azure-ai-projects)
