package com.backend.bank.security.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final ApplicationContext applicationContext;

//    private static final Marker AUTH_SUCCESS = MarkerManager.getMarker("AUTH_SUCCESS");
//
//    private static final Marker AUTH_FAILURE = MarkerManager.getMarker("AUTH_FAILURE");
//
//    private static final Marker JWT_EXCEPTION = MarkerManager.getMarker("JWT_EXCEPTION");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, java.io.IOException {
        try {
//            log.info("-----------------AUTH FILTERING----------------------");
//            loggingProcess(request, response, 1);

            String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String jwt;
            String username;

            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
//                log.info("-------------No valid token found-------");
//                loggingProcess(request, response,2);
                return;
            }

            jwt = requestTokenHeader.substring(7);
            username = jwtProvider.extractUserName(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = applicationContext.getBean(UserDetailsService.class).loadUserByUsername(username);

                if (jwtProvider.isTokenValid(jwt, userDetails)) {
//                    log.info("------------------TOKEN  IS VALID-----------------");
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken  = new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(), null, userDetails.getAuthorities()
                    );
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//                    log.info(AUTH_SUCCESS, "Authentication successful!");
//                    loggingProcess(request, response,3);
                } else {
//                    log.info(AUTH_FAILURE, "-----------------Invalid token!-----------------");
//                    loggingProcess(request, response,4);
                    throw new JwtException("Invalid JWT token");
                }
            }
            filterChain.doFilter(request, response);
//            log.info("#################Request processed-PASSED#################");
//            loggingProcess(request, response,5);
        }catch (IOException |ServletException e) {
//            log.error(JWT_EXCEPTION, e.getMessage(), e);
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            throw new JwtException(e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private void loggingProcess(HttpServletRequest request, HttpServletResponse response, int step) {
        log.info("""
                            [{}] - \
                           \s
                             Request Method: {} \
                           \s
                             Request Auth: {} \
                           \s
                             Request Principal: {} \
                           \s
                             Request URI: {} \
                           \s
                             Request Query: {} \
                            \s
                             Response Status: {}\
                            \s
                             Response Content: {}  \s
                            \s""",
                step,
                request.getMethod(),
                request.getAuthType(),
                request.getUserPrincipal(),
                request.getRequestURI(),
                request.getQueryString(),
                response.getStatus(),
                response.getContentType()
        );
    }
}