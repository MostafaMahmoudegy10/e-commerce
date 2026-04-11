package org.stylehub.backend.e_commerce.platform.security.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.stylehub.backend.e_commerce.platform.security.error.RestAccessDeniedHandler;
import org.stylehub.backend.e_commerce.platform.security.error.RestAuthenticationEntryPoint;
import org.stylehub.backend.e_commerce.platform.security.jwt.JwtAuthenticationConverter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(c -> {})
                .sessionManagement(s->s.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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
                .build();
    }
}
