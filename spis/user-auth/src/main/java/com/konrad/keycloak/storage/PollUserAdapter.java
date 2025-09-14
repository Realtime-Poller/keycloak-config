package com.konrad.keycloak.storage;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import java.util.List;
import java.util.stream.Stream;

public class PollUserAdapter extends AbstractUserAdapter {
    private final PollUser user;

    public PollUserAdapter(KeycloakSession session,
                           RealmModel realm,
                           ComponentModel model,
                           PollUser user) {
        super(session, realm, model);
        this.user = user;
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
        if (storageId == null) {
            storageId = new StorageId(storageProviderModel.getId(), this.user.getId().toString());
        }
        return storageId.getId();
    }

    @Override
    public SubjectCredentialManager credentialManager() {
        return new SubjectCredentialManager() {
            @Override
            public boolean isValid(List<CredentialInput> list) {
                return false;
            }

            @Override
            public boolean updateCredential(CredentialInput credentialInput) {
                return false;
            }

            @Override
            public void updateStoredCredential(CredentialModel credentialModel) {

            }

            @Override
            public CredentialModel createStoredCredential(CredentialModel credentialModel) {
                return null;
            }

            @Override
            public boolean removeStoredCredentialById(String s) {
                return false;
            }

            @Override
            public CredentialModel getStoredCredentialById(String s) {
                return null;
            }

            @Override
            public Stream<CredentialModel> getStoredCredentialsStream() {
                return Stream.empty();
            }

            @Override
            public Stream<CredentialModel> getStoredCredentialsByTypeStream(String s) {
                return Stream.empty();
            }

            @Override
            public CredentialModel getStoredCredentialByNameAndType(String s, String s1) {
                return null;
            }

            @Override
            public boolean moveStoredCredentialTo(String s, String s1) {
                return false;
            }

            @Override
            public void updateCredentialLabel(String s, String s1) {

            }

            @Override
            public void disableCredentialType(String s) {

            }

            @Override
            public Stream<String> getDisableableCredentialTypesStream() {
                return Stream.empty();
            }

            @Override
            public boolean isConfiguredFor(String s) {
                return false;
            }

            @Override
            public boolean isConfiguredLocally(String s) {
                return false;
            }

            @Override
            public Stream<String> getConfiguredUserStorageCredentialTypesStream() {
                return Stream.empty();
            }

            @Override
            public CredentialModel createCredentialThroughProvider(CredentialModel credentialModel) {
                return null;
            }
        };
    }
}