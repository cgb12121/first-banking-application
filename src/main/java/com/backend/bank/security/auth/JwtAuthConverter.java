//package com.backend.bank.security.auth;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.lang.NonNull;
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
//
//import java.util.Collection;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Configuration
//public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
//
//    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter=new JwtGrantedAuthoritiesConverter();
//
//    @Override
//    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
//        Collection<GrantedAuthority> authorities = Stream.concat(
//                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
//                extractResourceRoles(jwt).stream()
//        ).collect(Collectors.toSet());
//        return new JwtAuthenticationToken(jwt, authorities,jwt.getClaim("preferred_username"));
//    }
//
//    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
//        Map<String, Object> realmAccess;
//        Collection<String> roles;
//
//        if (jwt.getClaim("realm_access") == null) {
//            return Set.of();
//        }
//
//        realmAccess = jwt.getClaim("realm_access");
//
//        Object rolesObj = realmAccess.get("roles");
//        if (rolesObj instanceof Collection<?>) {
//            roles = ((Collection<?>) rolesObj).stream()
//                    .filter(item -> item instanceof String)
//                    .map(item -> (String) item)
//                    .collect(Collectors.toSet());
//        } else {
//            roles = Set.of();
//        }
//
//        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
//    }
//
//}