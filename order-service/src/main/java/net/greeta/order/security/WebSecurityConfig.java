package net.greeta.order.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests

                        .requestMatchers(HttpMethod.POST, "", "/", "/**").hasAnyRole(ORDER_MANAGER, ORDER_USER)
                        .requestMatchers(HttpMethod.GET, "/users/me", "/users/meExists").hasAnyRole(ORDER_MANAGER, ORDER_USER)
                        .requestMatchers("/users", "/users/**").hasRole(ORDER_MANAGER)
                        .requestMatchers("/public/**", "/auth/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .requestMatchers("", "/", "/**").hasAnyRole(ORDER_MANAGER, ORDER_USER)

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .build();
    }

    public static final String ORDER_MANAGER = "ORDER_MANAGER";
    public static final String ORDER_USER = "ORDER_USER";
}