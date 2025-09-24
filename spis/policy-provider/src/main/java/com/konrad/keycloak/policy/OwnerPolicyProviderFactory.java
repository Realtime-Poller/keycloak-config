package com.konrad.keycloak.policy;

import org.keycloak.Config;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.policy.provider.PolicyProvider;
import org.keycloak.authorization.policy.provider.PolicyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class OwnerPolicyProviderFactory implements PolicyProviderFactory<OwnerPolicyRepresentation> {

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
    public OwnerPolicyRepresentation toRepresentation(Policy policy, AuthorizationProvider authorization) {
        OwnerPolicyRepresentation rep = new OwnerPolicyRepresentation();
        rep.setName(policy.getName());
        rep.setDescription(policy.getDescription());
        return rep;
    }

    @Override
    public Class<OwnerPolicyRepresentation> getRepresentationType() {
        return OwnerPolicyRepresentation.class;
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
