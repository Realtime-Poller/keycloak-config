package com.konrad.keycloak.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.storage.UserStorageProviderFactory;
import java.util.List;
import java.util.Properties;

public class PollUserStorageProviderFactory implements UserStorageProviderFactory<PollUserStorageProvider> {
    public static final String PROVIDER_NAME = "poll-app-postgres-v1-provider";

    private HikariDataSource dataSource;

    @Override
    public void init(Config.Scope scope) {
        Properties properties = new Properties();
        properties.setProperty("jdbcUrl", scope.get("jdbcUrl"));
        properties.setProperty("username", scope.get("dbUsername"));
        properties.setProperty("password", scope.get("dbPassword"));

        properties.setProperty("maximumPoolSize", "10");
        properties.setProperty("minimumIdle", "5");
        properties.setProperty("idleTimeout", "60000");
        properties.setProperty("connectionTimeout", "30000");
        properties.setProperty("maxLifetime", "1800000");
        properties.setProperty("poolName", "poll-app-postgres-v1-pool");

        HikariConfig config = new HikariConfig(properties);
        dataSource = new HikariDataSource(config);
    }

    @Override
    public PollUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new PollUserStorageProvider(session, model, this.dataSource);
    }

    @Override
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
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
