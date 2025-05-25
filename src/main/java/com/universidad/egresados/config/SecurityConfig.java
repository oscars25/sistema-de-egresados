package com.universidad.egresados.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.time.Duration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2ResourceServerProperties properties) throws Exception {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/auth/**", "/perfil", "/registro", "/contacto",
                        "/css/**", "/js/**", "/img/**", "/public/**", "/login").permitAll()

                // Cambiados a MAYÚSCULAS
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/empresa/**").hasRole("EMPRESA")
                .requestMatchers("/egresado/**").hasRole("EGRESADO")

                // Solo admin o empresa pueden crear/editar/eliminar
                .requestMatchers("/ofertas/crear", "/ofertas/editar/**", "/ofertas/eliminar/**")
                    .hasAnyRole("ADMIN", "EMPRESA")

                // Egresado solo puede ver o aplicar
                .requestMatchers("/ofertas/ver/**", "/ofertas/aplicar/**").hasRole("EGRESADO")

                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/perfil", true)
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(new OidcUserService())
                )
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtConverter)
                    .decoder(jwtDecoder(properties))
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("https://dev-6v1unrgk3o6a56fo.us.auth0.com/v2/logout?client_id=0NARD1y0LJkjHhKZ80oYIWl6FOBH8GcA&returnTo=https://sistema-de-egresados.onrender.com/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    // Usa el namespace real y roles en mayúsculas
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("ROLE_"); // ROLE_ADMIN, ROLE_EMPRESA...
        converter.setAuthoritiesClaimName("https://egresados.ues.sv/claims/roles");
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder)
            JwtDecoders.fromIssuerLocation(properties.getJwt().getIssuerUri());

        OAuth2TokenValidator<Jwt> withClockSkew = new DelegatingOAuth2TokenValidator<>(
            new JwtTimestampValidator(Duration.ofMinutes(5))
        );

        jwtDecoder.setJwtValidator(withClockSkew);
        return jwtDecoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
