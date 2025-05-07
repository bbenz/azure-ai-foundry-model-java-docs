package com.azure.ai.foundry;

import com.azure.ai.projects.AIProjectClient;
import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.models.File;
import com.azure.ai.projects.models.FileSearchTool;
import com.azure.ai.projects.models.FilePurpose;
import com.azure.ai.projects.models.Message;
import com.azure.ai.projects.models.Run;
import com.azure.ai.projects.models.Thread;
import com.azure.ai.projects.models.VectorStore;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.ai.foundry.util.EnvironmentUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Sample application demonstrating file search capabilities in Azure AI Foundry.
 * This sample shows how to:
 * - Upload files to Azure AI Foundry
 * - Create vector stores from the uploaded files
 * - Create agents with file search tools
 * - Query agents about the content of the uploaded files
 */
public class FileSearchApp {

    private static AIProjectClient projectClient;
    private static String modelDeploymentName;

    public static void main(String[] args) {
        try {
            // Check required environment variables
            if (!EnvironmentUtil.checkRequiredEnv("AZURE_AI_ENDPOINT", "MODEL_DEPLOYMENT_NAME")) {
                System.err.println("Please set all required environment variables in your .env file");
                System.exit(1);
            }

            // Initialize AI Project client
            String endpoint = EnvironmentUtil.getEnv("AZURE_AI_ENDPOINT");
            modelDeploymentName = EnvironmentUtil.getEnv("MODEL_DEPLOYMENT_NAME");
            
            DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();
            projectClient = new AIProjectClientBuilder()
                .endpoint(endpoint)
                .credential(credential)
                .buildClient();
            
            System.out.println("Azure AI Project client initialized successfully.");
            
            // Run the file search demonstration
            runFileSearchExample();
            
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates the file search capabilities of Azure AI Foundry.
     */
    private static void runFileSearchExample() throws IOException {
        System.out.println("\n=== Running File Search Example ===");
        
        // Create and upload multiple files
        Path[] filePaths = createSampleFiles();
        List<File> uploadedFiles = uploadFiles(filePaths);
        System.out.println("Uploaded " + uploadedFiles.size() + " files");
        
        // Create a vector store with the uploaded files
        List<String> fileIds = uploadedFiles.stream()
            .map(File::getId)
            .toList();
        
        VectorStore vectorStore = projectClient.getAgents().createVectorStoreAndPoll(
            fileIds, 
            "product_documents_vectorstore");
        
        System.out.println("Created vector store with ID: " + vectorStore.getId());
        
        try {
            // Create a file search tool
            FileSearchTool fileSearch = new FileSearchTool(List.of(vectorStore.getId()));
            
            // Create an agent with file search capabilities
            var agent = projectClient.getAgents().createAgent(
                modelDeploymentName,
                "document-search-agent",
                "You are an assistant that helps users find information in their documents. " +
                "Use the search tool to look up relevant information in the uploaded files. " +
                "If you find the information in the documents, cite the document name as your source.",
                fileSearch.getDefinitions(),
                fileSearch.getResources());
            
            System.out.println("Created agent with ID: " + agent.getId());
            
            // Create a thread for conversation
            Thread thread = projectClient.getAgents().createThread();
            System.out.println("Created thread with ID: " + thread.getId());
            
            try {
                // Ask a series of questions about the documents
                runQuestionAndAnswer(thread.getId(), agent.getId(), 
                    "What products does Contoso offer?");
                
                runQuestionAndAnswer(thread.getId(), agent.getId(), 
                    "What are the key features of the Surface Laptop?");
                
                runQuestionAndAnswer(thread.getId(), agent.getId(), 
                    "Tell me about Contoso's cloud security features.");
                
                runQuestionAndAnswer(thread.getId(), agent.getId(), 
                    "What is included in the Contoso Office Suite?");
                
            } finally {
                // Clean up the agent
                projectClient.getAgents().deleteAgent(agent.getId());
                System.out.println("Deleted agent: " + agent.getId());
            }
            
        } finally {
            // Clean up the vector store and files
            projectClient.getAgents().deleteVectorStore(vectorStore.getId());
            System.out.println("Deleted vector store: " + vectorStore.getId());
            
            for (File file : uploadedFiles) {
                projectClient.getAgents().deleteFile(file.getId());
                System.out.println("Deleted file: " + file.getId());
            }
            
            // Delete the local files
            for (Path filePath : filePaths) {
                Files.deleteIfExists(filePath);
            }
        }
    }
    
    /**
     * Runs a question-and-answer interaction with the agent.
     */
    private static void runQuestionAndAnswer(String threadId, String agentId, String question) {
        System.out.println("\n--- New Question ---");
        System.out.println("User: " + question);
        
        // Add the user's question to the thread
        Message message = projectClient.getAgents().createMessage(
            threadId, 
            "user", 
            question);
        
        // Process the agent run
        Run run = projectClient.getAgents().createAndProcessRun(threadId, agentId);
        
        if ("failed".equals(run.getStatus())) {
            System.out.println("Run failed: " + run.getLastError());
            return;
        }
        
        // Get the agent's response
        var messages = projectClient.getAgents().listMessages(threadId);
        var lastMsg = messages.getLastTextMessageByRole("assistant");
        
        if (lastMsg != null) {
            System.out.println("Assistant: " + lastMsg.getText().getValue());
        } else {
            System.out.println("No response from assistant");
        }
    }
    
    /**
     * Creates sample files with different content for demonstration.
     */
    private static Path[] createSampleFiles() throws IOException {
        // Product information file (same as in QuickstartApp)
        String productInfo = """
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
        
        // Cloud security information
        String cloudSecurity = """
            # Contoso Cloud Security Features
            
            Contoso Cloud Services provides enterprise-grade security features to protect your data and applications.
            
            ## Security Capabilities
            
            - **Multi-factor Authentication**: Protect access to your cloud resources with multiple layers of
              authentication including SMS, email, and authenticator apps.
              
            - **Advanced Encryption**: All data is encrypted at rest and in transit using AES-256 encryption
              standards to ensure maximum protection.
              
            - **Identity and Access Management**: Fine-grained control over who can access what resources,
              with role-based access control and just-in-time privileged access.
              
            - **Network Security**: Firewall protection, DDoS mitigation, and private VNet connectivity
              to isolate your resources from the public internet.
              
            - **Security Monitoring**: 24/7 security monitoring with automated threat detection and
              response capabilities to identify and mitigate potential security incidents.
              
            - **Compliance Certifications**: Contoso Cloud is certified for various compliance standards
              including ISO 27001, SOC 2, HIPAA, and GDPR.
            
            ## Security Best Practices
            
            Contoso provides detailed guidance and tools to help customers implement security best practices:
            
            1. Regularly review access permissions
            2. Enable logging and monitoring
            3. Implement least privilege principles
            4. Configure automatic patching
            5. Use private endpoints where possible
            """;
        
        // Product pricing information
        String pricingInfo = """
            # Contoso Product Pricing Information
            
            ## Surface Laptop Pricing
            
            | Model               | Processor    | RAM  | Storage | Price (USD) |
            |---------------------|--------------|------|---------|-------------|
            | Surface Laptop Core | Intel Core i5| 8GB  | 256GB   | $999        |
            | Surface Laptop Plus | Intel Core i5| 16GB | 512GB   | $1,299      |
            | Surface Laptop Pro  | Intel Core i7| 16GB | 512GB   | $1,499      |
            | Surface Laptop Max  | Intel Core i7| 32GB | 1TB     | $1,999      |
            
            *All models include a 1-year warranty and free shipping.*
            
            ## Contoso Cloud Services Pricing
            
            ### Compute Services
            
            - **Basic VM**: $0.012 per hour ($8.76 per month)
            - **Standard VM**: $0.048 per hour ($35.04 per month)
            - **Premium VM**: $0.096 per hour ($70.08 per month)
            
            ### Storage Services
            
            - **Object Storage**: $0.02 per GB per month
            - **Block Storage**: $0.10 per GB per month
            - **Archive Storage**: $0.004 per GB per month
            
            ### Database Services
            
            - **Managed SQL**: Starting at $0.17 per hour
            - **NoSQL Database**: $0.25 per GB per month, plus throughput costs
            
            ## Contoso Office Suite Pricing
            
            - **Basic Plan**: $5.99 per user per month
            - **Standard Plan**: $12.99 per user per month
            - **Premium Plan**: $19.99 per user per month
            - **Enterprise Plan**: $24.99 per user per month
            
            *Volume discounts available for purchases of 10+ licenses.*
            """;
        
        // Create files
        Path productInfoPath = Paths.get("product_info.md");
        Path cloudSecurityPath = Paths.get("cloud_security.md");
        Path pricingInfoPath = Paths.get("pricing_info.md");
        
        Files.writeString(productInfoPath, productInfo);
        Files.writeString(cloudSecurityPath, cloudSecurity);
        Files.writeString(pricingInfoPath, pricingInfo);
        
        return new Path[] { productInfoPath, cloudSecurityPath, pricingInfoPath };
    }
    
    /**
     * Uploads multiple files to Azure AI Foundry.
     */
    private static List<File> uploadFiles(Path[] filePaths) {
        return Arrays.stream(filePaths)
            .map(path -> {
                try {
                    File file = projectClient.getAgents().uploadFile(
                        path.toString(), 
                        FilePurpose.AGENTS);
                    System.out.println("Uploaded file: " + path.getFileName() + " with ID: " + file.getId());
                    return file;
                } catch (Exception e) {
                    System.err.println("Failed to upload file " + path + ": " + e.getMessage());
                    throw new RuntimeException(e);
                }
            })
            .toList();
    }
}
