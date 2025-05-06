package com.example.ai.projects.spring.service;

import com.azure.ai.projects.models.Deployment;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for chatting with AI using Spring AI and Azure SDK for Java.
 */
@Service
public class AIChatService {
    
    private final ChatClient chatClient;
    private final AzureAIService aiService;
    
    public AIChatService(ChatClient chatClient, AzureAIService aiService) {
        this.chatClient = chatClient;
        this.aiService = aiService;
    }
    
    /**
     * Chat with the AI.
     * @param userMessage The user message.
     * @return The AI response.
     */
    public String chat(String userMessage) {
        return chatClient.call(userMessage);
    }
      /**
     * Chat about a specific deployment.
     * @param deploymentName The name of the deployment.
     * @return The AI response.
     */
    public String chatAboutDeployment(String deploymentName) {
        try {            // Get deployment info using Azure SDK for Java
            Deployment deployment = aiService.getDeployment(deploymentName);
            
            // Create a system prompt with deployment information
            String systemPromptTemplate = "You are an AI assistant that provides information about Azure SDK for Java. " +
                "Here's information about a deployment named {{name}}:\n" +
                "- Type: {{type}}\n" +
                "- Created at: {{createdAt}}\n" +
                "Please answer user questions about this deployment.";
            
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", deployment.getName());
            variables.put("type", deployment.getType().getValue());
            variables.put("createdAt", deployment.getCreatedAt().toString());
            
            Message systemMessage = new SystemPromptTemplate(systemPromptTemplate).create(variables);
            Message userMessage = new UserMessage("Tell me about this deployment");
            
            List<Message> messages = new ArrayList<>();
            messages.add(systemMessage);
            messages.add(userMessage);
            
            Prompt prompt = new Prompt(messages);
            ChatResponse response = chatClient.call(prompt);
            
            return response.getResult().getOutput().getContent();
        } catch (Exception e) {
            return "Error getting information about deployment: " + e.getMessage();
        }
    }
}
