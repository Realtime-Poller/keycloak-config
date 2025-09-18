package com.konrad.keycloak.storage;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.List;
import java.util.stream.Stream;

public class PollUserAdapter extends AbstractUserAdapterFederatedStorage {
    private final PollUser user;
    private String storageId;

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
            storageId = new StorageId(storageProviderModel.getId(), this.user.getId().toString()).getId();
        }
        return storageId;
    }

    @Override
    public void setUsername(String username) {
        // Read-only implementation - changes not persisted back to external DB
        user.setEmail(username);
    }

    @Override
    public void setEmail(String email) {
        // Read-only implementation - changes not persisted back to external DB
        user.setEmail(email);
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
        // Delegate to federated storage
        super.setSingleAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        // Delegate to federated storage
        super.removeAttribute(name);
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        // Delegate to federated storage
        super.setAttribute(name, values);
    }

    @Override
    public String getFirstAttribute(String name) {
        // Delegate to federated storage
        return super.getFirstAttribute(name);
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        // Delegate to federated storage
        return super.getAttributeStream(name);
    }

    @Override
    public SubjectCredentialManager credentialManager() {
        return new SubjectCredentialManager() {
            @Override
            public boolean isValid(List<CredentialInput> inputs) {
                for(CredentialInput input : inputs) {
                    if(PasswordCredentialModel.TYPE.equals(input.getType())) {
                        String plaintextPassword = input.getChallengeResponse();
                        String hashedPasswordFromDb = user.getPassword();

                        BCrypt.Result result = BCrypt.verifyer().verify(plaintextPassword.toCharArray(), hashedPasswordFromDb);
                        if(result.verified) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean updateCredential(CredentialInput credentialInput) {
                return false;
            }

            @Override
            public void updateStoredCredential(CredentialModel credentialModel) {
                // Not implemented for external storage
            }

            @Override
            public CredentialModel createStoredCredential(CredentialModel credentialModel) {
                // Not implemented for external storage
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
                // Not implemented
            }

            @Override
            public void disableCredentialType(String s) {
                // Not implemented
            }

            @Override
            public Stream<String> getDisableableCredentialTypesStream() {
                return Stream.empty();
            }

            @Override
            public boolean isConfiguredFor(String type) {
                // Only password authentication is supported
                return PasswordCredentialModel.TYPE.equals(type);
            }

            @Override
            public boolean isConfiguredLocally(String s) {
                return false;
            }

            @Override
            public Stream<String> getConfiguredUserStorageCredentialTypesStream() {
                return Stream.of(PasswordCredentialModel.TYPE);
            }

            @Override
            public CredentialModel createCredentialThroughProvider(CredentialModel credentialModel) {
                return null;
            }
        };
    }
}