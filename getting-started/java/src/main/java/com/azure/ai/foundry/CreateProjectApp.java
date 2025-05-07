package com.azure.ai.foundry;

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.cognitiveservices.CognitiveServicesManager;
import com.azure.resourcemanager.cognitiveservices.models.Account;
import com.azure.resourcemanager.cognitiveservices.models.ApiProperties;
import com.azure.resourcemanager.cognitiveservices.models.Identity;
import com.azure.resourcemanager.cognitiveservices.models.IdentityType;
import com.azure.resourcemanager.cognitiveservices.models.Kind;
import com.azure.resourcemanager.cognitiveservices.models.ResourceProvisioningState;
import com.azure.resourcemanager.cognitiveservices.models.Sku;
import com.azure.resourcemanager.resources.ResourceManager;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Sample showing how to create an Azure AI Foundry project using the Azure SDK for Java.
 */
public class CreateProjectApp {

    public static void main(String[] args) {
        try {
            // Load environment variables
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            
            String subscriptionId = dotenv.get("AZURE_SUBSCRIPTION_ID");
            String resourceGroupName = dotenv.get("AZURE_RESOURCE_GROUP");
            String foundryResourceName = dotenv.get("AZURE_FOUNDRY_RESOURCE_NAME");
            String foundryProjectName = dotenv.get("AZURE_FOUNDRY_PROJECT_NAME");
            String location = dotenv.get("AZURE_REGION", "eastus");
            
            // Validate required parameters
            validateParameters(subscriptionId, resourceGroupName, foundryResourceName, foundryProjectName);
            
            // Set up authentication
            TokenCredential credential = new DefaultAzureCredentialBuilder().build();
            AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
            
            System.out.println("Creating Azure AI Foundry resources...");
            
            // Create Resource Manager client
            ResourceManager resourceManager = ResourceManager.authenticate(credential, profile)
                .withSubscription(subscriptionId);
            
            // Check if resource group exists, create if it doesn't
            ResourceGroup resourceGroup;
            if (!resourceManager.resourceGroups().contain(resourceGroupName)) {
                System.out.println("Creating resource group: " + resourceGroupName);
                resourceGroup = resourceManager.resourceGroups()
                    .define(resourceGroupName)
                    .withRegion(location)
                    .create();
                System.out.println("Resource group created: " + resourceGroup.name());
            } else {
                System.out.println("Resource group already exists: " + resourceGroupName);
                resourceGroup = resourceManager.resourceGroups().getByName(resourceGroupName);
            }
            
            // Create the Cognitive Services Manager
            CognitiveServicesManager cognitiveServicesManager = CognitiveServicesManager.authenticate(
                credential, profile);
            
            // Check if Foundry resource already exists
            boolean resourceExists = cognitiveServicesManager.accounts().listByResourceGroup(resourceGroupName)
                .stream()
                .anyMatch(account -> account.name().equalsIgnoreCase(foundryResourceName));
            
            if (resourceExists) {
                System.out.println("Foundry resource already exists: " + foundryResourceName);
                System.out.println("Please use a different name or delete the existing resource.");
                return;
            }
            
            // Create the account
            System.out.println("Creating AI Foundry resource: " + foundryResourceName);
            Account account = cognitiveServicesManager.accounts()
                .define(foundryResourceName)
                .withRegion(location)
                .withExistingResourceGroup(resourceGroupName)
                .withKind(Kind.AI_SERVICES)
                .withSku(new Sku().withName("S0"))
                .withIdentity(new Identity().withType(IdentityType.SYSTEM_ASSIGNED))
                .withProperties(new ApiProperties().withAllowProjectManagement(true))
                .create();
            
            System.out.println("AI Foundry resource created:");
            System.out.println("  - Name: " + account.name());
            System.out.println("  - Type: " + account.type());
            System.out.println("  - Location: " + account.location());
            System.out.println("  - Provisioning State: " + account.properties().provisioningState());
            
            // Wait for resource to be fully provisioned
            int attempts = 0;
            while (!ResourceProvisioningState.SUCCEEDED.equals(account.properties().provisioningState()) 
                   && attempts < 30) {
                System.out.println("Waiting for resource to be fully provisioned...");
                Thread.sleep(10000); // Wait 10 seconds
                account = cognitiveServicesManager.accounts().getById(account.id());
                attempts++;
            }
            
            if (!ResourceProvisioningState.SUCCEEDED.equals(account.properties().provisioningState())) {
                System.out.println("Resource provisioning did not complete successfully within the timeout period.");
                return;
            }
            
            System.out.println("AI Foundry resource successfully provisioned!");
            System.out.println("\nNext steps:");
            System.out.println("1. Log in to the Azure Portal");
            System.out.println("2. Navigate to your AI Foundry resource");
            System.out.println("3. Create a new project named '" + foundryProjectName + "'");
            System.out.println("4. Deploy models and set up your environment");
            System.out.println("5. Update your .env file with the project endpoint");
            
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Validates that required parameters are provided.
     */
    private static void validateParameters(String subscriptionId, String resourceGroupName, 
                                          String foundryResourceName, String foundryProjectName) {
        if (subscriptionId == null || subscriptionId.isEmpty() || subscriptionId.equals("your-subscription-id")) {
            throw new IllegalArgumentException("AZURE_SUBSCRIPTION_ID is required. Update your .env file.");
        }
        
        if (resourceGroupName == null || resourceGroupName.isEmpty() || resourceGroupName.equals("your-resource-group-name")) {
            throw new IllegalArgumentException("AZURE_RESOURCE_GROUP is required. Update your .env file.");
        }
        
        if (foundryResourceName == null || foundryResourceName.isEmpty() || foundryResourceName.equals("your-foundry-resource-name")) {
            throw new IllegalArgumentException("AZURE_FOUNDRY_RESOURCE_NAME is required. Update your .env file.");
        }
        
        if (foundryProjectName == null || foundryProjectName.isEmpty() || foundryProjectName.equals("your-foundry-project-name")) {
            throw new IllegalArgumentException("AZURE_FOUNDRY_PROJECT_NAME is required. Update your .env file.");
        }
    }
}
