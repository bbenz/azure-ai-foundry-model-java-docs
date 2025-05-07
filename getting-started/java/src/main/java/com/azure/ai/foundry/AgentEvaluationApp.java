package com.azure.ai.foundry;

import com.azure.ai.projects.AIProjectClient;
import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.EvaluatorIds;
import com.azure.ai.projects.models.AgentEvaluationResult;
import com.azure.ai.projects.models.EvaluationOutput;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.ai.foundry.util.EnvironmentUtil;

import java.util.List;
import java.util.Map;

/**
 * Sample application demonstrating detailed agent evaluation functionality.
 * This sample shows how to evaluate agent runs with different evaluators and
 * interpret the evaluation results.
 */
public class AgentEvaluationApp {

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
            
            // Run the basic agent with a simple task that we'll evaluate
            String threadId = null;
            String runId = null;
            String agentId = null;
            
            try {
                // Create a simple agent
                var agent = projectClient.getAgents().createAgent(
                    modelDeploymentName,
                    "evaluation-test-agent",
                    "You are an AI assistant specializing in knowledge of world capitals. " +
                    "Always provide accurate information about capital cities of countries. " +
                    "If you're not sure about an answer, acknowledge this rather than guessing.");
                
                agentId = agent.getId();
                System.out.println("Created agent with ID: " + agentId);
                
                // Create a thread
                var thread = projectClient.getAgents().createThread();
                threadId = thread.getId();
                System.out.println("Created thread with ID: " + threadId);
                
                // Add messages to simulate a conversation
                projectClient.getAgents().createMessage(
                    threadId, "user", "What is the capital of France?");
                
                // Process the agent run
                var run = projectClient.getAgents().createAndProcessRun(threadId, agentId);
                runId = run.getId();
                System.out.println("Run completed with status: " + run.getStatus());
                
                // Print the conversation
                System.out.println("\n--- Conversation ---");
                var messages = projectClient.getAgents().listMessages(threadId);
                for (var message : messages.getTextMessages()) {
                    System.out.println(message);
                }
                System.out.println("-------------------\n");
                
                // Evaluate the agent run with multiple evaluators
                runSimpleEvaluation(threadId, runId);
                runDetailedEvaluation(threadId, runId);
                
            } finally {
                // Clean up resources if created
                if (agentId != null) {
                    projectClient.getAgents().deleteAgent(agentId);
                    System.out.println("Deleted agent: " + agentId);
                }
            }
            
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Runs a simple evaluation with the agent quality evaluator.
     */
    private static void runSimpleEvaluation(String threadId, String runId) {
        System.out.println("\n=== Running Simple Agent Evaluation ===");
        
        try {
            // Create the evaluation
            var result = projectClient.getEvaluation().createAgentEvaluation(
                threadId,
                runId,
                List.of(EvaluatorIds.AGENT_QUALITY_EVALUATOR));
            
            // Wait for the evaluation to complete
            result.waitForCompletion();
            
            // Access the evaluation output
            EvaluationOutput output = result.output();
            System.out.println("Evaluation completed with result:");
            
            // Print the scores
            if (output != null && output.getScores() != null) {
                for (Map.Entry<String, Object> score : output.getScores().entrySet()) {
                    System.out.println("  " + score.getKey() + ": " + score.getValue());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error in simple evaluation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Runs a detailed evaluation with multiple evaluators and analyzes the results.
     */
    private static void runDetailedEvaluation(String threadId, String runId) {
        System.out.println("\n=== Running Detailed Agent Evaluation ===");
        
        try {
            // Create a detailed evaluation with multiple evaluators
            AgentEvaluationResult result = projectClient.getEvaluation().createAgentEvaluation(
                threadId,
                runId,
                List.of(
                    EvaluatorIds.AGENT_QUALITY_EVALUATOR,
                    EvaluatorIds.GROUNDEDNESS_EVALUATOR,
                    EvaluatorIds.HARMFULNESS_EVALUATOR
                ));
            
            // Wait for the evaluation to complete
            result.waitForCompletion();
            
            // Get the evaluation output
            EvaluationOutput output = result.output();
            
            if (output != null) {
                System.out.println("\nDetailed evaluation results:");
                
                // Print overall score if available
                if (output.getScores() != null) {
                    System.out.println("\nOverall Scores:");
                    for (Map.Entry<String, Object> entry : output.getScores().entrySet()) {
                        System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                    }
                }
                
                // Print evaluators and their results
                if (output.getResults() != null) {
                    System.out.println("\nEvaluator Results:");
                    for (Map.Entry<String, Map<String, Object>> evaluator : output.getResults().entrySet()) {
                        System.out.println("\n  Evaluator: " + evaluator.getKey());
                        
                        Map<String, Object> evaluatorResults = evaluator.getValue();
                        for (Map.Entry<String, Object> result : evaluatorResults.entrySet()) {
                            System.out.println("    " + result.getKey() + ": " + result.getValue());
                        }
                    }
                }
                
                // Print any feedback or analysis
                if (output.getFeedback() != null) {
                    System.out.println("\nEvaluation Feedback:");
                    System.out.println(output.getFeedback());
                }
            } else {
                System.out.println("No evaluation output available.");
            }
            
        } catch (Exception e) {
            System.err.println("Error in detailed evaluation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
