package com.konrad.keycloak.policy;

import org.keycloak.authorization.policy.evaluation.Evaluation;
import org.keycloak.authorization.policy.provider.PolicyProvider;

public class OwnerPolicyProvider implements PolicyProvider {

    @Override
    public void evaluate(Evaluation evaluation) {
        var context = evaluation.getContext();
        var identity = context.getIdentity();

        String userId = identity.getId();

        var attributes = evaluation.getPermission().getResource().getAttributes();
        String owner = null;
        if (attributes.containsKey("owner")) {
            owner = attributes.get("owner").get(0);
        }

        if (owner != null && owner.equals(userId)) {
            evaluation.grant();
        }
    }

    @Override
    public void close() {
        // nothing to clean up
    }
}
