package com.konrad.keycloak.storage;

import com.zaxxer.hikari.HikariDataSource;
import org.jboss.logging.Logger;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.federated.UserAttributeFederatedStorage;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;



public class PollUserStorageProvider implements
        UserLookupProvider,
        UserStorageProvider,
        UserQueryProvider,
        UserAttributeFederatedStorage {
    private final KeycloakSession session;
    private final ComponentModel model;
    private final HikariDataSource dataSource;

    private final Logger logger = Logger.getLogger(getClass());

    public PollUserStorageProvider(KeycloakSession session,
                                   ComponentModel model,
                                   HikariDataSource dataSource) {
        this.session = session;
        this.model = model;
        this.dataSource = dataSource;
    }

    private UserModel findUserByEmail(RealmModel realmModel, String email) {
        final String SELECT_USER_BY_EMAIL =
                "SELECT id, email, password, createdTimestamp, lastUpdatedTimestamp" +
                        " FROM USERS" +
                        " WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SELECT_USER_BY_EMAIL)) {
            preparedStatement.setString(1, email);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if(rs.next()) {
                    logger.debug("User found in database, creating PollUserAdapter.");
                    Instant created = Optional.ofNullable(rs.getTimestamp("createdTimestamp"))
                            .map(Timestamp::toInstant)
                            .orElse(null);

                    Instant updated = Optional.ofNullable(rs.getTimestamp("lastUpdatedTimestamp"))
                            .map(Timestamp::toInstant)
                            .orElse(null);
                    PollUser pollUser = new PollUser(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            created,
                            updated);
                    return new PollUserAdapter(session, realmModel, model, pollUser);
                }
            }
        } catch (Exception e) {
            logger.error("Error when searching for user by email", e);
        }
        return null;
    }

    @Override
    public UserModel getUserById(RealmModel realmModel, String id) {
        String externalId = StorageId.externalId(id);
        if(externalId == null) {
            return null;
        }
        Long userId;
        try {
            userId = Long.parseLong(externalId);
        } catch (NumberFormatException e) {
            return null;
        }

        return findUserById(realmModel, userId);
    }

    private UserModel findUserById(RealmModel realmModel, Long userId) {
        final String SELECT_USER_BY_ID =
                "SELECT id, email, password, createdTimestamp, lastUpdatedTimestamp" +
                        " FROM USERS" +
                        " WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SELECT_USER_BY_ID)) {
            preparedStatement.setLong(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if(rs.next()) {
                    Instant created = Optional.ofNullable(rs.getTimestamp("createdTimestamp"))
                            .map(Timestamp::toInstant)
                            .orElse(null);

                    Instant updated = Optional.ofNullable(rs.getTimestamp("lastUpdatedTimestamp"))
                            .map(Timestamp::toInstant)
                            .orElse(null);

                    PollUser pollUser = new PollUser(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            created,
                            updated);
                    return new PollUserAdapter(session, realmModel, model, pollUser);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String username) {
        return getUserByEmail(realmModel, username);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String email) {
        return findUserByEmail(realmModel, email);
    }



    @Override
    public CredentialValidationOutput getUserByCredential(RealmModel realm, CredentialInput input) {
        return UserLookupProvider.super.getUserByCredential(realm, input);
    }

    @Override
    public void close() {
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realmModel, Map<String, String> map, Integer firstResult, Integer maxResults) {
        try {
            String searchString = map.get(UserModel.SEARCH);
            logger.debugf("Searching for users with parameters: %s", map);

            if (searchString == null || searchString.trim().isEmpty()) {
                logger.debug("Search string is null or empty, returning empty stream");
                return Stream.empty();
            }

            searchString = searchString.trim();
            logger.debugf("Attempting to find user by email: %s", searchString);

            UserModel user = findUserByEmail(realmModel, searchString);

            if (user != null) {
                logger.debugf("Found user: %s", user.getId());
                return Stream.of(user);
            } else {
                logger.debugf("No user found for search: %s", searchString);
                return Stream.empty();
            }
        } catch (Exception e) {
            logger.error("Error during user search", e);
            return Stream.empty();
        }
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realmModel, GroupModel groupModel, Integer integer, Integer integer1) {
        return Stream.empty();
    }

    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realmModel, String attrName, String attrValue) {
        if (attrName.equalsIgnoreCase("email")) {
            UserModel user = findUserByEmail(realmModel, attrValue);
            return user != null ? Stream.of(user) : Stream.empty();
        }
        return Stream.empty();
    }

    @Override
    public void setSingleAttribute(RealmModel realmModel, String s, String s1, String s2) {

    }

    @Override
    public void setAttribute(RealmModel realmModel, String s, String s1, List<String> list) {

    }

    @Override
    public void removeAttribute(RealmModel realmModel, String s, String s1) {

    }

    @Override
    public MultivaluedHashMap<String, String> getAttributes(RealmModel realmModel, String s) {
        return new MultivaluedHashMap<>();
    }

    @Override
    public Stream<String> getUsersByUserAttributeStream(RealmModel realm, String name, String value) {
        if ("email".equalsIgnoreCase(name)) {
            UserModel user = findUserByEmail(realm, value);
            if (user != null) {
                return Stream.of(user.getId());
            }
        }
        return Stream.empty();
    }
}
