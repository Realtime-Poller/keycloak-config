package com.konrad.keycloak.storage;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LegacyUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        UserQueryProvider {
    private final KeycloakSession session;
    private final ComponentModel model;
    private final OkHttpClient httpClient;

    public LegacyUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
        this.httpClient = new OkHttpClient();
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        // The URL points to our mock-api container by its service name
        System.out.println("Getting user " + username + " from legacy API");
        Request request = new Request.Builder()
                .url("http://mock-legacy-api:1080/users/" + username)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                return null; // User not found
            }
            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);

            LegacyUser user = new LegacyUser();
            user.setId(json.getString("id"));
            user.setUsername(json.getString("username"));
            user.setEmail(json.getString("email"));
            user.setFirstName(json.getString("firstName"));
            user.setLastName(json.getString("lastName"));

            return new LegacyUserAdapter(session, realm, model, user);
        } catch (IOException e) {
            // In a real provider, you'd log this error
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        // For now, we only support lookup by username
        return null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        // For now, we only support lookup by username
        return null;
    }

    @Override
    public void close() {
        // Nothing to close
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        // This is the primary method called by the Admin UI.
        // Let's log the parameters to see what Keycloak is sending us.
        System.out.println("Advanced search with params: " + params);

        // The Admin UI search term often comes in as the 'search' parameter or a specific field like 'username'.
        String search = params.get(UserModel.SEARCH);
        if (search == null) {
            search = params.get(UserModel.USERNAME);
        }

        if (search == null) {
            // If there are no search terms we can handle, return an empty result.
            return Stream.empty();
        }

        // Now delegate to our simple search logic.
        return searchForUserStream(realm, search, firstResult, maxResults);
    }


    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        // This method contains our actual logic for finding a user.
        System.out.println("Simple search with string: " + search);

        List<UserModel> users = new ArrayList<>();
        UserModel user = getUserByUsername(realm, search);
        if (user != null) {
            users.add(user);
        }
        // This correctly returns a Stream from a List, which ensures Keycloak will create a JSON array.
        return users.stream();
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, org.keycloak.models.GroupModel group, Integer firstResult, Integer maxResults) {
        return Stream.empty(); // Not implemented
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return Stream.empty(); // Not implemented
    }
}
