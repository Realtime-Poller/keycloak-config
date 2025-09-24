package com.konrad.keycloak.policy;

import org.keycloak.representations.idm.authorization.AbstractPolicyRepresentation;

public class OwnerPolicyRepresentation extends AbstractPolicyRepresentation {

    @Override
    public String getType() {
        return OwnerPolicyProviderFactory.PROVIDER_ID;
    }
}
