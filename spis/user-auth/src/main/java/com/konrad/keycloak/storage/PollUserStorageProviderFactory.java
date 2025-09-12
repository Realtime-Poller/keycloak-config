package com.konrad.keycloak.storage;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.storage.UserStorageProviderFactory;
import java.util.List;

public class PollUserStorageProviderFactory implements UserStorageProviderFactory<PollUserStorageProvider> {
    public static final String PROVIDER_NAME = "user-storage-provider";

    @Override
    public PollUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new PollUserStorageProvider(session, model);
    }

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        List<ProviderConfigProperty> properties;
        ProviderConfigProperty url = new ProviderConfigProperty();
        url.setName("jdbcUrl");
        url.setLabel("JDBC URL");
        url.setType(ProviderConfigProperty.STRING_TYPE);
        url.setHelpText("JDBC URL");

        ProviderConfigProperty username = new ProviderConfigProperty();
        username.setName("dbUsername");
        username.setLabel("Username");
        username.setType(ProviderConfigProperty.STRING_TYPE);
        username.setHelpText("Username");

        ProviderConfigProperty password = new ProviderConfigProperty();
        password.setName("dbPassword");
        password.setLabel("Password");
        password.setType(ProviderConfigProperty.PASSWORD);
        password.setHelpText("Password");

        properties = List.of(url, username, password);
        return properties;
    }
}
