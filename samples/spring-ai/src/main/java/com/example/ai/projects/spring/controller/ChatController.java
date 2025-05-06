package com.example.ai.projects.spring.controller;

import com.example.ai.projects.spring.service.AIChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for AI chat operations.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    private final AIChatService chatService;
    
    public ChatController(AIChatService chatService) {
        this.chatService = chatService;
    }
    
    /**
     * Chat with the AI.
     * @param message The user message.
     * @return The AI response.
     */
    @PostMapping
    public ResponseEntity<String> chat(@RequestBody String message) {
        return ResponseEntity.ok(chatService.chat(message));
    }
    
    /**
     * Chat about a specific deployment.
     * @param name The name of the deployment.
     * @return The AI response.
     */
    @GetMapping("/deployments/{name}")
    public ResponseEntity<String> chatAboutDeployment(@PathVariable String name) {
        return ResponseEntity.ok(chatService.chatAboutDeployment(name));
    }
}
