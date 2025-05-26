package com.universidad.egresados.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
                .requestMatchers("/", "/auth/**", "/registro", "/contacto", "/css/**", "/js/**", "/img/**", "/public/**", "/login").permitAll()

                // Acceso para ADMIN, EMPRESA y EGRESADO al perfil
                .requestMatchers("/perfil").hasAnyRole("ADMIN", "EMPRESA", "EGRESADO")

                // Rutas para ADMIN y EMPRESA para crear, editar, eliminar ofertas
                .requestMatchers("/ofertas/crear", "/ofertas/editar/**", "/ofertas/eliminar/**")
                    .hasAnyRole("ADMIN", "EMPRESA")

                // Rutas para ADMIN, EMPRESA y EGRESADO para ver y aplicar ofertas
                .requestMatchers("/ofertas", "/ofertas/aplicar/**")
                    .hasAnyRole("ADMIN", "EMPRESA", "EGRESADO")

                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/perfil", true)
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(customOidcUserService()) // Custom para convertir roles en authorities
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

    /**
     * Extrae los roles personalizados del claim del namespace y los convierte en authorities con prefijo ROLE_
     */
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("ROLE_");
        converter.setAuthoritiesClaimName("https://egresados.ues.sv/claims/roles");
        return converter;
    }

    /**
     * Configura el decodificador JWT con una tolerancia de reloj.
     */
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

    /**
     * Convierte los roles del claim personalizado también para login con oauth2Login
     */
    private OidcUserService customOidcUserService() {
        OidcUserService delegate = new OidcUserService();

        return new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) {
                OidcUser oidcUser = delegate.loadUser(userRequest);
                Collection<GrantedAuthority> mappedAuthorities = new ArrayList<>(oidcUser.getAuthorities());

                Object rolesClaim = oidcUser.getClaims().get("https://egresados.ues.sv/claims/roles");
                if (rolesClaim instanceof List<?> roles) {
                    for (Object role : roles) {
                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString()));
                    }
                }

                return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            }
        };
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
