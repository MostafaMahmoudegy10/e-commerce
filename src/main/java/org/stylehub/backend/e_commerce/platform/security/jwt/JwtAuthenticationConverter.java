package org.stylehub.backend.e_commerce.platform.security.jwt;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final static Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationConverter.class);
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<String> normalizedRoles = new HashSet<>();

        List<String> rolesClaim = jwt.getClaimAsStringList("roles");
        if (rolesClaim != null) {
            normalizedRoles.addAll(rolesClaim);
        }

        String singleRoleClaim = jwt.getClaimAsString("role");
        if (singleRoleClaim != null && !singleRoleClaim.isBlank()) {
            normalizedRoles.add(singleRoleClaim);
        }

        Set<GrantedAuthority> finalRoles =normalizedRoles.stream()
                .map(this::normalizeRole)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        LOGGER.info("Authorities: {}", finalRoles);
        return finalRoles;
    }

    private String normalizeRole(String rawRole) {
        String upper = rawRole.trim().toUpperCase(Locale.ROOT);

        if ("USER".equals(upper) || "CUSTOMER".equals(upper)) {
            return "CUSTOMER";
        }

        if ("BRAND".equals(upper)  || "BRAND_OWNER".equals(upper)) {
            return "BRAND_OWNER";
        }

        return upper;
    }
}
