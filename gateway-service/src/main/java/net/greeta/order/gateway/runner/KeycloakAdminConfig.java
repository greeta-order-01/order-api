package net.greeta.order.gateway.runner;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class KeycloakAdminConfig {


    @Bean
    public Keycloak keycloakAdmin(@Value("${spring.keycloak.server-url}") String serverUrl) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .username("admin")
                .password("admin")
                .clientId("admin-cli")
                .build();
    }
}
