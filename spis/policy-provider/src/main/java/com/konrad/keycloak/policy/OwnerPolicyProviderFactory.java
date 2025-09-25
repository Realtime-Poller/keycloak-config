package com.konrad.keycloak.policy;

import org.keycloak.Config;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.policy.provider.PolicyProvider;
import org.keycloak.authorization.policy.provider.PolicyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import org.keycloak.representations.idm.authorization.PolicyRepresentation;

public class OwnerPolicyProviderFactory implements PolicyProviderFactory<PolicyRepresentation> {

    public static final String PROVIDER_ID = "com-konrad-owner-policy";

    private final OwnerPolicyProvider provider = new OwnerPolicyProvider();

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getName() {
        return "Owner Policy";
    }

    @Override
    public String getGroup() {
        return "Identity";
    }

    @Override
    public PolicyProvider create(AuthorizationProvider authorizationProvider) {
        return this.provider;
    }

    @Override
    public PolicyProvider create(KeycloakSession keycloakSession) {
        return this.provider;
    }

    @Override
    public PolicyRepresentation toRepresentation(Policy policy, AuthorizationProvider authorization) {
        PolicyRepresentation rep = new PolicyRepresentation();
        rep.setName(policy.getName());
        rep.setDescription(policy.getDescription());
        rep.setType(PROVIDER_ID);
        return rep;
    }

    @Override
    public Class<PolicyRepresentation> getRepresentationType() {
        return PolicyRepresentation.class;
    }

    @Override
    public void onCreate(Policy policy, PolicyRepresentation representation, AuthorizationProvider authorization) {
        // This remains empty as there's no custom configuration to save.
    }

    @Override
    public void init(Config.Scope config) {
        // No init needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No post init
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
