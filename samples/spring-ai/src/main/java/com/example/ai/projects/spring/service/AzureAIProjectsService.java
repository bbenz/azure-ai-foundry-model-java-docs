// filepath: c:\githublocal\azure-ai-foundry-model-java-docs\samples\spring-ai\src\main\java\com\example\ai\projects\spring\service\AzureAIService.java
package com.example.ai.projects.spring.service;

import com.azure.ai.projects.ConnectionsClient;
import com.azure.ai.projects.DeploymentsClient;
import com.azure.ai.projects.models.Connection;
import com.azure.ai.projects.models.Deployment;
import com.azure.ai.projects.models.ListViewType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Azure SDK for Java operations.
 */
@Service
public class AzureAIService {
    
    private final ConnectionsClient connectionsClient;    private final DeploymentsClient deploymentsClient;
    
    public AzureAIService(ConnectionsClient connectionsClient, DeploymentsClient deploymentsClient) {
        this.connectionsClient = connectionsClient;
        this.deploymentsClient = deploymentsClient;
    }
      /**
     * List all connections using Azure SDK for Java.
     * @return A list of connections.
     */
    public List<Connection> listConnections() {
        List<Connection> connections = new ArrayList<>();
        connectionsClient.list(null, ListViewType.ALL).forEach(connections::add);
        return connections;
    }
    
    /**
     * Get a specific connection by name.
     * @param connectionName The name of the connection.
     * @return The connection.
     */
    public Connection getConnection(String connectionName) {
        return connectionsClient.get(connectionName);
    }
      /**
     * List all deployments using Azure SDK for Java.
     * @return A list of deployments.
     */
    public List<Deployment> listDeployments() {
        List<Deployment> deployments = new ArrayList<>();
        deploymentsClient.list(null, ListViewType.ALL).forEach(deployments::add);
        return deployments;
    }
    
    /**
     * Get a specific deployment by name.
     * @param deploymentName The name of the deployment.
     * @return The deployment.
     */
    public Deployment getDeployment(String deploymentName) {
        return deploymentsClient.get(deploymentName);
    }
}
