package com.konrad.keycloak.storage;

import com.zaxxer.hikari.HikariDataSource;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.sql.*;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class PollUserStorageProvider implements
        UserLookupProvider,
        UserStorageProvider,
        UserQueryProvider {
    private final KeycloakSession session;
    private final ComponentModel model;
    private final HikariDataSource dataSource;

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
    public Stream<UserModel> searchForUserStream(RealmModel realmModel, Map<String, String> map, Integer integer, Integer integer1) {
        String searchString = map.get(UserModel.SEARCH);

        if (searchString == null) {
            return Stream.empty();
        }

        UserModel user = findUserByEmail(realmModel, searchString.trim());

        return user != null ? Stream.of(user) : Stream.empty();
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
}
