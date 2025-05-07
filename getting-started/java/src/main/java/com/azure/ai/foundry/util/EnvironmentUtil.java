package com.azure.ai.foundry.util;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Utility class for environment variable management.
 */
public class EnvironmentUtil {

    private static Dotenv dotenv;
    
    // Ensure we only load the environment once
    static {
        dotenv = Dotenv.configure().ignoreIfMissing().load();
    }
    
    /**
     * Gets an environment variable.
     * 
     * @param key The environment variable key
     * @return The value of the environment variable or null if not found
     */
    public static String getEnv(String key) {
        return dotenv.get(key);
    }
    
    /**
     * Gets an environment variable with a default value.
     * 
     * @param key The environment variable key
     * @param defaultValue The default value if the environment variable is not found
     * @return The value of the environment variable or the default value if not found
     */
    public static String getEnv(String key, String defaultValue) {
        String value = dotenv.get(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
    
    /**
     * Checks if all the required environment variables are set.
     * 
     * @param keys The environment variable keys to check
     * @return true if all keys are set, false otherwise
     */
    public static boolean checkRequiredEnv(String... keys) {
        for (String key : keys) {
            String value = getEnv(key);
            if (value == null || value.isEmpty()) {
                System.err.println("Missing required environment variable: " + key);
                return false;
            }
        }
        return true;
    }
}
