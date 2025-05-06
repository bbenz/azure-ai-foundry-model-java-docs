package com.example.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Environment post processor that loads environment variables from a .env file
 * for Spring Boot applications. This class is automatically registered and 
 * executed during Spring Boot startup.
 * 
 * To enable this component, create a file at:
 * META-INF/spring.factories with the content:
 * org.springframework.boot.env.EnvironmentPostProcessor=com.example.util.DotenvProcessor
 */
@Component
public class DotenvProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "dotenvProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Load .env file using dotenv-java
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // Don't throw exception if .env file is not found
                .load();

        // Create a map with all values from .env
        Map<String, Object> propertySource = new HashMap<>();
        
        // Add all dotenv variables to the property source
        dotenv.entries().forEach(entry -> {
            propertySource.put(entry.getKey(), entry.getValue());
        });

        // Add the property source to the environment
        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, propertySource));
    }
}
