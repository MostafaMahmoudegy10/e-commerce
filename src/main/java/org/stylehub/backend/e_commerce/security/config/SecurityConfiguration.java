package org.stylehub.backend.e_commerce.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.stylehub.backend.e_commerce.exception.security.RestAccessDeniedHandler;
import org.stylehub.backend.e_commerce.exception.security.RestAuthenticationEntryPoint;
import org.stylehub.backend.e_commerce.security.filters.LocalUserSyncFilter;
import org.stylehub.backend.e_commerce.security.jwt.JwtAuthenticationConverter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final LocalUserSyncFilter  localUserSyncFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s->s.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/api/v1/public/**", "/error").permitAll()
                .requestMatchers("/api/v1/brands/**").hasRole("BRAND_OWNER") // a brand owner
                .requestMatchers("/api/v1/customer/**").hasAnyRole("CUSTOMER", "BRAND_OWNER") // customer
                .anyRequest().authenticated()
                 ).oauth2ResourceServer(o->
                 o.jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtAuthenticationConverter()))
                         .authenticationEntryPoint(authenticationEntryPoint)
                         .accessDeniedHandler(accessDeniedHandler)
                ).exceptionHandling(ex->
                        ex.authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler))
                .addFilterAfter(localUserSyncFilter, BearerTokenAuthenticationFilter.class)
                .build();
    }
}
