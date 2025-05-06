package com.example.util;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Utility class for handling environment variables in the Spring AI samples.
 * Loads variables from .env file with fallback to system environment variables.
 */
public class EnvironmentConfig {
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    /**
     * Get an environment variable with a fallback value.
     * 
     * @param key The environment variable name
     * @param defaultValue The default value to use if the environment variable is not set
     * @return The environment variable value or the default value
     */
    public static String get(String key, String defaultValue) {
        String value = dotenv.get(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return value != null ? value : defaultValue;
    }

    /**
     * Get an environment variable (required).
     * Throws an IllegalArgumentException if the variable is not set.
     * 
     * @param key The environment variable name
     * @return The environment variable value
     * @throws IllegalArgumentException if the environment variable is not set
     */
    public static String getRequired(String key) {
        String value = get(key, null);
        if (value == null) {
            throw new IllegalArgumentException(
                    "Required environment variable '" + key + "' is not set. " +
                    "Please set it in your .env file or system environment variables.");
        }
        return value;
    }

    /**
     * Get the Azure AI endpoint URL from environment variables.
     * 
     * @return The Azure AI endpoint URL
     */
    public static String getAzureAIEndpoint() {
        return getRequired("AZURE_AI_ENDPOINT");
    }

    /**
     * Get the connection name from environment variables.
     * 
     * @return The connection name
     */
    public static String getConnectionName() {
        return getRequired("CONNECTION_NAME");
    }

    /**
     * Get the deployment name from environment variables.
     * 
     * @return The deployment name
     */
    public static String getDeploymentName() {
        return getRequired("DEPLOYMENT_NAME");
    }
}
