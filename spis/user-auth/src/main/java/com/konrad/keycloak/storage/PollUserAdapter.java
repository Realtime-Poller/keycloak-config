package com.konrad.keycloak.storage;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class PollUserAdapter extends AbstractUserAdapterFederatedStorage {
    private final PollUser user;
    private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

    public PollUserAdapter(KeycloakSession session,
                           RealmModel realm,
                           ComponentModel model,
                           PollUser user) {
        super(session, realm, model);
        this.user = user;
        setAttribute("email", List.of(user.getEmail()));
        setAttribute("username", List.of(user.getEmail()));
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public String getId() {
        if (this.storageId == null) {
            this.storageId = new StorageId(storageProviderModel.getId(), this.user.getId().toString());
        }
        return this.storageId.getId();
    }

    @Override
    public void setUsername(String username) {
        List<String> singletonList = List.of(username);
        attributes.put("email", singletonList);
    }

    @Override
    public void setEmail(String email) {
        List<String> singletonList = List.of(email);
        attributes.put("username", singletonList);
    }

    @Override
    public void setEmailVerified(boolean verified) {
        // Not supported in this implementation
    }

    @Override
    public boolean isEmailVerified() {
        return true;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        List<String> singletonList = List.of(value);
        attributes.put(name, singletonList);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        attributes.put(name, values);
    }

    @Override
    public String getFirstAttribute(String name) {
        Object value = attributes.get(name);

        if (value instanceof List) {
            List<String> attributeValues = (List<String>) value;

            if (!attributeValues.isEmpty()) {
                return attributeValues.get(0);
            }
        }
        return null;
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        Object value = attributes.get(name);

        if( value instanceof List ) {
            List<String> attributeValues = (List<String>) value;
            if(!attributeValues.isEmpty()) {
                return attributeValues.stream();
            }
        }
        return Stream.empty();
    }
}