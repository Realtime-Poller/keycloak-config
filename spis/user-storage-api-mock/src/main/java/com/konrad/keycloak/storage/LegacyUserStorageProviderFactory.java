package com.konrad.keycloak.storage;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class LegacyUserStorageProviderFactory implements UserStorageProviderFactory<LegacyUserStorageProvider> {

    public static final String PROVIDER_ID = "legacy-user-storage-ords";

    @Override
    public LegacyUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new LegacyUserStorageProvider(session, model);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
