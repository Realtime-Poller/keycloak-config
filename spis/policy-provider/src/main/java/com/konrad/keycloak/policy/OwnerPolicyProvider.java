package com.konrad.keycloak.policy;

import org.keycloak.authorization.policy.evaluation.Evaluation;
import org.keycloak.authorization.policy.provider.PolicyProvider;

import java.util.Collection;
import java.util.Map;

public class OwnerPolicyProvider implements PolicyProvider {

    @Override
    public void evaluate(Evaluation evaluation) {
        var context = evaluation.getContext();
        var identity = context.getIdentity();

        String keycloakUserId = identity.getId();
        String[] parts = keycloakUserId.split(":");
        String userId = parts.length > 0 ? parts[parts.length - 1] : null;

        Map<String, Collection<String>> attributes = context.getAttributes().toMap();

        String claimKey = "owner";
        String owner = null;

        if (attributes.containsKey(claimKey)) {
            owner = attributes.get(claimKey).stream().findFirst().orElse(null);
        }

        if (owner != null && owner.equals(userId)) {
            evaluation.grant();
        } else {
            evaluation.deny();
        }
    }

    @Override
    public void close() {
        // nothing to clean up
    }
}
