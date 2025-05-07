# Azure AI Foundry Java Samples

This directory contains Java-based samples for Azure AI Foundry, showing how to use the Azure SDK for Java to interact with AI Foundry services.

## Prerequisites

- Java Development Kit (JDK) 17 or later
- Maven 3.6.0 or later
- An Azure subscription with access to Azure AI services
- Access to Azure AI Foundry

## Setup

1. Copy the `.env.template` file to a new file named `.env`
2. Edit the `.env` file to include your Azure credentials and endpoint information

## Samples

The following samples are available:

### QuickstartApp.java

A basic sample demonstrating:
- Setting up the Azure AI Foundry client
- Running a chat completion
- Creating and running an agent
- Using file search with an agent
- Evaluating an agent run

### CreateProjectApp.java

A sample demonstrating how to create an Azure AI Foundry project and resources using the Azure SDK for Java.

### FileSearchApp.java

A dedicated sample focusing on file search capabilities:
- Uploading multiple files with different content
- Creating vector stores from the files
- Creating agents with file search tools
- Running a question-and-answer sequence to demonstrate search capabilities

### AgentEvaluationApp.java

A sample focused on evaluating agent performance:
- Creating and running an agent with a specific knowledge domain
- Evaluating the agent using different evaluators
- Analyzing detailed evaluation results

### SimpleAgentExample.java

A streamlined example demonstrating the AgentHelper utility class:
- Creating and using simple agents without tools
- Creating and using agents with file search capabilities
- Managing agent resources and cleanup

## Utility Classes

### AgentHelper.java

A helper class that simplifies common agent operations:
- Creating simple agents
- Creating file search agents with automatic resource management
- Running conversations with agents
- Cleaning up agents and associated resources

### EnvironmentUtil.java

A utility class for environment variable management:
- Loading variables from .env files
- Accessing environment variables with optional default values
- Validating required environment variables

## Building and Running

Build the project with Maven:

```bash
mvn clean package
```

Run the QuickstartApp sample:

```bash
mvn exec:java -Dexec.mainClass="com.azure.ai.foundry.QuickstartApp"
```

Run the CreateProjectApp sample:

```bash
mvn exec:java -Dexec.mainClass="com.azure.ai.foundry.CreateProjectApp"
```

Run the FileSearchApp sample:

```bash
mvn exec:java -Dexec.mainClass="com.azure.ai.foundry.FileSearchApp"
```

Run the AgentEvaluationApp sample:

```bash
mvn exec:java -Dexec.mainClass="com.azure.ai.foundry.AgentEvaluationApp"
```

Run the SimpleAgentExample sample:

```bash
mvn exec:java -Dexec.mainClass="com.azure.ai.foundry.SimpleAgentExample"
```

## Additional Resources

- [Azure AI Foundry Documentation](https://learn.microsoft.com/azure/ai-foundry/)
- [Azure SDK for Java](https://github.com/Azure/azure-sdk-for-java)
- [Azure AI Projects SDK for Java](https://central.sonatype.com/artifact/com.azure/azure-ai-projects)
