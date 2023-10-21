package net.greeta.order.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                        .pathMatchers(HttpMethod.GET,"/webjars/**").permitAll()
                        .pathMatchers(HttpMethod.GET,"/", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .pathMatchers(HttpMethod.GET,"/swagger-resources/**").permitAll()
                        .pathMatchers(HttpMethod.GET,"/v3/api-docs/**").permitAll()

                        .pathMatchers("/erp", "/erp/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/order", "/order/**").hasAnyRole(ORDER_MANAGER, ORDER_USER)
                        .pathMatchers(HttpMethod.GET, "/order/users/me", "/order/users/meExists").hasAnyRole(ORDER_MANAGER, ORDER_USER)
                        .pathMatchers("/order/users", "/order/users/**").hasRole(ORDER_MANAGER)
                        .pathMatchers("/order/public", "/order/public/**", "/order/auth", "/order/auth/**").permitAll()
                        .pathMatchers("/order/swagger-ui.html", "/order/swagger-ui/**", "/order/v3/api-docs", "/order/v3/api-docs/**").permitAll()
                        .pathMatchers("/order", "/order/**").hasAnyRole(ORDER_MANAGER, ORDER_USER)

                        .anyExchange().authenticated()
                        .and()
                        .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
                        .oauth2ResourceServer().jwt()
                        .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(jwtAuthConverter))
                )
                //.csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
                .build();
    }

    @Bean
    WebFilter csrfWebFilter() {
        // Required because of https://github.com/spring-projects/spring-security/issues/5766
        return (exchange, chain) -> {
            exchange.getResponse().beforeCommit(() -> Mono.defer(() -> {
                Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
                return csrfToken != null ? csrfToken.then() : Mono.empty();
            }));
            return chain.filter(exchange);
        };
    }

    public static final String ORDER_MANAGER = "ORDER_MANAGER";
    public static final String ORDER_USER = "ORDER_USER";
}
