package org.stylehub.backend.e_commerce.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;

public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

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

        return normalizedRoles.stream()
                .map(this::normalizeRole)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
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