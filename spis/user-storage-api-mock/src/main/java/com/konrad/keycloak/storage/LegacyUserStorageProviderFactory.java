package com.konrad.keycloak.storage;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class LegacyUserStorageProviderFactory implements UserStorageProviderFactory<LegacyUserStorageProvider> {

    public static final String PROVIDER_ID = "legacy-user-storage-ords";

    @Override
    public LegacyUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        // Here you could pass configuration to the provider
        return new LegacyUserStorageProvider(session, model);
    }

    @Override
    public String getId() {
        // This is the unique ID of our provider, shown in the Admin Console
        return PROVIDER_ID;
    }
}
