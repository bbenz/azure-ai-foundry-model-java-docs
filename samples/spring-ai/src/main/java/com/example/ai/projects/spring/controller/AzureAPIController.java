package com.example.ai.projects.spring.controller;

import com.azure.ai.projects.models.Connection;
import com.azure.ai.projects.models.Deployment;
import com.example.ai.projects.spring.service.AzureAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Azure SDK for Java operations.
 */
@RestController
@RequestMapping("/api/ai")
public class AzureAPIController {
    
    private final AzureAIService aiService;
    
    public AzureAPIController(AzureAIService aiService) {
        this.aiService = aiService;
    }
    
    /**
     * List all connections.
     * @return A list of connections.
     */
    @GetMapping("/connections")
    public ResponseEntity<List<Connection>> listConnections() {
        return ResponseEntity.ok(aiService.listConnections());
    }
    
    /**
     * Get a specific connection by name.
     * @param name The name of the connection.
     * @return The connection.
     */
    @GetMapping("/connections/{name}")
    public ResponseEntity<Connection> getConnection(@PathVariable String name) {
        return ResponseEntity.ok(aiService.getConnection(name));
    }
    
    /**
     * List all deployments.
     * @return A list of deployments.
     */
    @GetMapping("/deployments")
    public ResponseEntity<List<Deployment>> listDeployments() {
        return ResponseEntity.ok(aiService.listDeployments());
    }
    
    /**
     * Get a specific deployment by name.
     * @param name The name of the deployment.
     * @return The deployment.
     */
    @GetMapping("/deployments/{name}")
    public ResponseEntity<Deployment> getDeployment(@PathVariable String name) {
        return ResponseEntity.ok(aiService.getDeployment(name));
    }
}
