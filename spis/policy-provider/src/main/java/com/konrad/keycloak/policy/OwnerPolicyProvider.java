package com.konrad.keycloak.policy;

import org.keycloak.authorization.policy.evaluation.Evaluation;
import org.keycloak.authorization.policy.provider.PolicyProvider;

import java.util.Collection;
import java.util.Map;

public class OwnerPolicyProvider implements PolicyProvider {

    @Override
    public void evaluate(Evaluation evaluation) {
        System.out.println("\n--- OwnerPolicyProvider: Evaluate method started (DEBUG MODE) ---");

        var context = evaluation.getContext();
        var identity = context.getIdentity();

        String keycloakUserId = identity.getId();
        String[] parts = keycloakUserId.split(":");
        String userId = parts.length > 0 ? parts[parts.length - 1] : null;

        System.out.println("[DEBUG] User ID from token (parsed) = '" + userId + "'");

        Map<String, Collection<String>> attributes = context.getAttributes().toMap();
        System.out.println("[DEBUG] --- START OF ALL CONTEXT ATTRIBUTES ---");
        attributes.forEach((key, value) ->
                System.out.println("[DEBUG] Attribute Key: " + key + ", Value: " + value)
        );
        System.out.println("[DEBUG] --- END OF ALL CONTEXT ATTRIBUTES ---");


        String claimKey = "owner";
        String owner = null;

        if (attributes.containsKey(claimKey)) {
            owner = attributes.get(claimKey).stream().findFirst().orElse(null);
        }

        System.out.println("[DEBUG] Owner ID from request claims (using " + claimKey + ") = '" + owner + "'");

        if (owner != null && owner.equals(userId)) {
            System.out.println("[SUCCESS] IDs match. Granting permission.");
            evaluation.grant();
        } else {
            System.out.println("[FAILURE] IDs do not match or claim missing. Denying permission.");
        }
        System.out.println("--- OwnerPolicyProvider: Evaluate method finished ---\n");
    }

    @Override
    public void close() {
        // nothing to clean up
    }
}
