package com.example.ai.projects;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.ai.projects.models.Deployment;
import com.azure.ai.projects.models.ListViewType;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

/**
 * Sample showing how to work with deployments using the Azure SDK for Java.
 */
public class DeploymentsSample {
    /**
     * Main method to demonstrate how to work with deployments using Azure SDK for Java.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {        // Create a client using DefaultAzureCredential
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
