package com.azure.ai.foundry;

import com.azure.ai.projects.AIProjectClient;
import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.EvaluatorIds;
import com.azure.ai.projects.models.FileSearchTool;
import com.azure.ai.projects.models.FilePurpose;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.openai.client.OpenAI;
import com.openai.client.completion.chat.ChatCompletion;
import com.openai.client.completion.chat.ChatCompletionRequest;
import com.openai.client.completion.chat.ChatMessage;
import com.openai.client.completion.chat.ChatMessageRole;
import io.github.cdimascio.dotenv.Dotenv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Azure AI Foundry Quickstart Sample.
 * This sample demonstrates basic usage of the Azure AI Foundry services using Java SDK.
 */
public class QuickstartApp {

    private static AIProjectClient projectClient;
    private static String modelDeploymentName;

    public static void main(String[] args) {
        try {
            // Load environment variables from .env file if it exists
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            
            // Initialize AI Project client
            String endpoint = dotenv.get("AZURE_AI_ENDPOINT");
            modelDeploymentName = dotenv.get("MODEL_DEPLOYMENT_NAME");
            
            if (endpoint == null || endpoint.isEmpty()) {
                System.err.println("Error: AZURE_AI_ENDPOINT environment variable is required.");
                System.exit(1);
            }
            
            if (modelDeploymentName == null || modelDeploymentName.isEmpty()) {
                System.err.println("Error: MODEL_DEPLOYMENT_NAME environment variable is required.");
                System.exit(1);
            }
            
            DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();
            projectClient = new AIProjectClientBuilder()
                .endpoint(endpoint)
                .credential(credential)
                .buildClient();
            
            System.out.println("Azure AI Project client initialized successfully.");
            
            // Run samples
            runChatCompletion();
            runBasicAgent();
            runFileSearchAgent();
            
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates how to use chat completion with Azure OpenAI.
     */
    private static void runChatCompletion() {
        System.out.println("\n=== Running Chat Completion Sample ===");
        
        try {
            // Get the OpenAI client from the project
            OpenAI openAIClient = projectClient.getInference().getAzureOpenAIClient("2024-06-01");
            
            // Create and send a chat completion request
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelDeploymentName)
                .messages(List.of(
                    new ChatMessage(ChatMessageRole.SYSTEM, "You are a helpful writing assistant"),
                    new ChatMessage(ChatMessageRole.USER, "Write me a poem about flowers")
                ))
                .build();
            
            ChatCompletion response = openAIClient.chatCompletions().create(request);
            
            // Print the response
            String content = response.getChoices().get(0).getMessage().getContent();
            System.out.println("Response from chat completion:");
            System.out.println(content);
            
        } catch (Exception e) {
            System.err.println("Error in chat completion sample: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates how to create and run a basic agent.
     */
    private static void runBasicAgent() {
        System.out.println("\n=== Running Basic Agent Sample ===");
        
        try {
            // Create an agent
            var agent = projectClient.getAgents().createAgent(
                modelDeploymentName,
                "my-agent",
                "You are a helpful writing assistant");
            
            System.out.println("Created agent with ID: " + agent.getId());
            
            // Create a thread for the conversation
            var thread = projectClient.getAgents().createThread();
            System.out.println("Created thread with ID: " + thread.getId());
            
            // Add a message to the thread
            var message = projectClient.getAgents().createMessage(
                thread.getId(), 
                "user", 
                "Write me a poem about flowers");
            
            System.out.println("Created message with ID: " + message.getId());
            
            // Create and process a run
            var run = projectClient.getAgents().createAndProcessRun(thread.getId(), agent.getId());
            System.out.println("Run status: " + run.getStatus());
            
            if ("failed".equals(run.getStatus())) {
                System.err.println("Run failed: " + run.getLastError());
            }
            
            // Get messages from the thread
            var messages = projectClient.getAgents().listMessages(thread.getId());
            
            // Get the last message from the assistant
            var lastMsg = messages.getLastTextMessageByRole("assistant");
            if (lastMsg != null) {
                System.out.println("Last message from assistant:");
                System.out.println(lastMsg.getText().getValue());
            }
            
            // Clean up
            projectClient.getAgents().deleteAgent(agent.getId());
            System.out.println("Deleted agent");
            
        } catch (Exception e) {
            System.err.println("Error in basic agent sample: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates how to create and use an agent with file search capabilities.
     */
    private static void runFileSearchAgent() {
        System.out.println("\n=== Running File Search Agent Sample ===");
        
        try {
            // Create a sample product information file for demonstration
            Path productInfoFile = createProductInfoFile();
            
            // Upload file to the agent service
            var file = projectClient.getAgents().uploadFile(
                productInfoFile.toString(), 
                FilePurpose.AGENTS);
            
            System.out.println("Uploaded file with ID: " + file.getId());
            
            // Create a vector store with the uploaded file
            var vectorStore = projectClient.getAgents().createVectorStoreAndPoll(
                List.of(file.getId()), 
                "my_vectorstore");
            
            System.out.println("Created vector store with ID: " + vectorStore.getId());
            
            // Create file search tool
            FileSearchTool fileSearch = new FileSearchTool(List.of(vectorStore.getId()));
            
            // Create agent with file search capabilities
            var agent = projectClient.getAgents().createAgent(
                modelDeploymentName,
                "my-assistant",
                "You are a helpful assistant and can search information from uploaded files",
                fileSearch.getDefinitions(),
                fileSearch.getResources());
            
            System.out.println("Created agent with ID: " + agent.getId());
            
            // Create thread and add user message
            var thread = projectClient.getAgents().createThread();
            System.out.println("Created thread with ID: " + thread.getId());
            
            projectClient.getAgents().createMessage(
                thread.getId(), 
                "user", 
                "Hello, what Contoso products do you know?");
            
            // Run the agent
            var run = projectClient.getAgents().createAndProcessRun(thread.getId(), agent.getId());
            System.out.println("Run status: " + run.getStatus());
            
            if ("failed".equals(run.getStatus())) {
                System.err.println("Run failed: " + run.getLastError());
            }
            
            // Print thread messages
            for (var msg : projectClient.getAgents().listMessages(thread.getId()).getTextMessages()) {
                System.out.println(msg);
            }
            
            // Evaluate the agent run
            evaluateAgentRun(thread.getId(), run.getId());
            
            // Clean up resources
            projectClient.getAgents().deleteVectorStore(vectorStore.getId());
            projectClient.getAgents().deleteFile(file.getId());
            projectClient.getAgents().deleteAgent(agent.getId());
            Files.deleteIfExists(productInfoFile);
            
            System.out.println("Cleaned up all resources");
            
        } catch (Exception e) {
            System.err.println("Error in file search agent sample: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Evaluates an agent run using the AGENT_QUALITY_EVALUATOR.
     */
    private static void evaluateAgentRun(String threadId, String runId) {
        System.out.println("\n=== Evaluating Agent Run ===");
        
        try {
            var result = projectClient.getEvaluation().createAgentEvaluation(
                threadId,
                runId,
                List.of(EvaluatorIds.AGENT_QUALITY_EVALUATOR));
            
            // Wait for the evaluation to complete
            result.waitForCompletion();
            
            // Print the evaluation result
            System.out.println("Evaluation result:");
            System.out.println(result.output().toString());
            
        } catch (Exception e) {
            System.err.println("Error in agent evaluation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a sample product information file for demonstration.
     */
    private static Path createProductInfoFile() throws Exception {
        String content = """
            # Contoso Products Information
            
            ## Surface Laptop
            The Surface Laptop is a premium laptop designed by Contoso, featuring a high-resolution
            touchscreen display, powerful processor, and long battery life. It comes in various colors
            and configurations to suit different needs.
            
            **Key Features:**
            - 13.5-inch PixelSense Display
            - Intel Core i5/i7 Processor
            - Up to 16GB RAM and 1TB SSD
            - Up to 17 hours of battery life
            - Windows 11 Pro
            
            ## Contoso Cloud Services
            Contoso Cloud Services provides scalable, secure, and reliable cloud computing solutions
            for businesses of all sizes. Our platform offers a wide range of services including
            computing, storage, database, and AI/ML capabilities.
            
            **Services Offered:**
            - Virtual Machines
            - Managed Databases
            - Object Storage
            - AI and Machine Learning
            - IoT Solutions
            
            ## Contoso Office Suite
            Contoso Office Suite is a comprehensive productivity software package that includes
            word processing, spreadsheet, presentation, and email applications. It's available
            as a subscription service with regular updates and cloud storage.
            
            **Applications Included:**
            - Contoso Word
            - Contoso Excel
            - Contoso PowerPoint
            - Contoso Outlook
            - Contoso Teams
            """;
        
        Path filePath = Paths.get("product_info_1.md");
        Files.writeString(filePath, content);
        return filePath;
    }
}
