package com.example.security;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

/**
 * Example showing how to securely access secrets from Azure Key Vault
 * for production environments.
 */
public class KeyVaultSecurityExample {

    private final SecretClient secretClient;

    /**
     * Initialize the KeyVaultSecurityExample with a Key Vault URL.
     * 
     * @param keyVaultUrl The URL of your Azure Key Vault (e.g., https://your-vault.vault.azure.net/)
     */
    public KeyVaultSecurityExample(String keyVaultUrl) {
        // Create a secret client using DefaultAzureCredential
        // This will use the most appropriate authentication method based on the environment
        this.secretClient = new SecretClientBuilder()
            .vaultUrl(keyVaultUrl)
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildClient();
    }

    /**
     * Retrieve a secret from Azure Key Vault.
     * 
     * @param secretName The name of the secret to retrieve
     * @return The secret value
     */
    public String getSecret(String secretName) {
        KeyVaultSecret secret = secretClient.getSecret(secretName);
        return secret.getValue();
    }

    /**
     * Example of how to use this class to retrieve secrets for Azure AI.
     */
    public static void main(String[] args) {
        // Key Vault URL from environment variable
        String keyVaultUrl = System.getenv("AZURE_KEY_VAULT_URL");
        if (keyVaultUrl == null || keyVaultUrl.isEmpty()) {
            System.err.println("AZURE_KEY_VAULT_URL environment variable is not set");
            System.exit(1);
        }

        try {
            KeyVaultSecurityExample securityExample = new KeyVaultSecurityExample(keyVaultUrl);

            // Retrieve secrets from Key Vault
            String azureAIEndpoint = securityExample.getSecret("azure-ai-endpoint");
            String connectionName = securityExample.getSecret("connection-name");
            
            System.out.println("Azure AI Endpoint: " + azureAIEndpoint);
            System.out.println("Connection Name: " + connectionName);
            
            // Use these secure credentials in your application
            // This is just an example - in real code, you'd pass these values to your client
            
        } catch (Exception e) {
            System.err.println("Error retrieving secrets from Key Vault: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
