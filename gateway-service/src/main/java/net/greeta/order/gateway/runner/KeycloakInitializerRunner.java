package net.greeta.order.gateway.runner;

import lombok.extern.slf4j.Slf4j;
import net.greeta.order.gateway.security.WebSecurityConfig;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class KeycloakInitializerRunner implements CommandLineRunner {

    @Autowired
    private Keycloak keycloakAdmin;

    @Value("${spring.keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${order-app.redirect-url}")
    private String orderAppRedirectUrl;

    @Override
    public void run(String... args) {
        log.info("Initializing '{}' realm in Keycloak ...", ORDER_REALM_NAME);

        Optional<RealmRepresentation> representationOptional = keycloakAdmin.realms()
                .findAll()
                .stream()
                .filter(r -> r.getRealm().equals(ORDER_REALM_NAME))
                .findAny();
        if (representationOptional.isPresent()) {
            log.info("Removing already pre-configured '{}' realm", ORDER_REALM_NAME);
            keycloakAdmin.realm(ORDER_REALM_NAME).remove();
        }

        // Realm
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(ORDER_REALM_NAME);
        realmRepresentation.setEnabled(true);
        realmRepresentation.setRegistrationAllowed(true);

        // Client
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(ORDER_APP_CLIENT_ID);
        clientRepresentation.setImplicitFlowEnabled(true);
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setPublicClient(true);
        clientRepresentation.setRedirectUris(List.of(orderAppRedirectUrl));
        clientRepresentation.setWebOrigins(List.of("*"));
        clientRepresentation.setDefaultRoles(new String[]{WebSecurityConfig.ORDER_USER});
        realmRepresentation.setClients(List.of(clientRepresentation));

        // Users
        List<UserRepresentation> userRepresentations = ORDER_APP_USERS.stream()
                .map(userPass -> {
                    // User Credentials
                    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                    credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                    credentialRepresentation.setValue(userPass.password());

                    // User
                    UserRepresentation userRepresentation = new UserRepresentation();
                    userRepresentation.setUsername(userPass.username());
                    userRepresentation.setEnabled(true);
                    userRepresentation.setCredentials(List.of(credentialRepresentation));
                    userRepresentation.setClientRoles(getClientRoles(userPass));
                    userRepresentation.setEmail(userPass.email());

                    return userRepresentation;
                })
                .toList();
        realmRepresentation.setUsers(userRepresentations);

        // Create Realm
        keycloakAdmin.realms().create(realmRepresentation);

        // Testing
        UserPass admin = ORDER_APP_USERS.get(0);
        log.info("Testing getting token for '{}' ...", admin.username());

        Keycloak keycloakErpApp = KeycloakBuilder.builder().serverUrl(keycloakServerUrl)
                .realm(ORDER_REALM_NAME).username(admin.username()).password(admin.password())
                .clientId(ORDER_APP_CLIENT_ID).build();

        log.info("'{}' token: {}", admin.username(), keycloakErpApp.tokenManager().grantToken().getToken());
        log.info("'{}' initialization completed successfully!", ORDER_REALM_NAME);
    }

    private Map<String, List<String>> getClientRoles(UserPass userPass) {
        List<String> roles = new ArrayList<>();
        roles.add(WebSecurityConfig.ORDER_USER);
        if ("admin".equals(userPass.username())) {
            roles.add(WebSecurityConfig.ORDER_MANAGER);
        }
        return Map.of(ORDER_APP_CLIENT_ID, roles);
    }

    private static final String ORDER_REALM_NAME = "order-realm";
    private static final String ORDER_APP_CLIENT_ID = "order-app";
    private static final List<UserPass> ORDER_APP_USERS = Arrays.asList(
            new UserPass("admin", "admin", "admin@example.com"),
            new UserPass("user", "user", "user@example.com"));

    private record UserPass(String username, String password, String email) {
    }
}