package net.greeta.order.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class SwaggerOpenIdConfig {

        private static final String OPEN_ID_SCHEME_NAME = "openId";
        private static final String OPENID_CONFIG_FORMAT = "%s/realms/%s/.well-known/openid-configuration";

        @Bean
        OpenAPI customOpenApi(@Value("${openapi.oauthflow.authServerUrl}") String authServerUrl,
                              @Value("${openapi.oauthflow.realm}") String realm) {
                return new OpenAPI()
                        .components(new Components()
                                .addSecuritySchemes(OPEN_ID_SCHEME_NAME, createOpenIdScheme(authServerUrl, realm)))
                        .addSecurityItem(new SecurityRequirement().addList(OPEN_ID_SCHEME_NAME));
        }

        private SecurityScheme createOpenIdScheme(String authServerUrl, String realm) {
                String connectUrl = String.format(OPENID_CONFIG_FORMAT, authServerUrl, realm);

                return new SecurityScheme()
                        .type(SecurityScheme.Type.OPENIDCONNECT)
                        .openIdConnectUrl(connectUrl);
        }
}