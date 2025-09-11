package com.konrad.keycloak.mappers;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;

public class StaticClaimMapper extends  AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper{
    public static final String PROVIDER_ID = "static-claim-mapper";

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getDisplayType() {
        return "Static Claim Mapper";
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "A simple mapper that adds a hardcoded claim 'example_claim: hello_world' to the token.";
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession, ClientSessionContext clientSessionCtx) {
        token.getOtherClaims().put("example_claim", "hello_world");
    }
}
