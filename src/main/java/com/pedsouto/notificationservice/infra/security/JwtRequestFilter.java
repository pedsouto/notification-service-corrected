package com.pedsouto.notificationservice.infra.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String INTERNAL_KEY_HEADER = "X-Internal-Service-Key";

    private final JwtUtil jwtUtil;
    private final String internalApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if ("/email".equals(request.getServletPath()) && !hasValidInternalKey(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso restrito à comunicação entre serviços");
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring(7);
                String email = jwtUtil.extractUsername(token);

                if (email == null || email.isBlank()) {
                    throw new IllegalArgumentException("O token não possui um usuário válido");
                }

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException | IllegalArgumentException ex) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido ou expirado");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean hasValidInternalKey(HttpServletRequest request) {
        String providedKey = request.getHeader(INTERNAL_KEY_HEADER);
        if (providedKey == null || internalApiKey == null) {
            return false;
        }

        return MessageDigest.isEqual(
                providedKey.getBytes(StandardCharsets.UTF_8),
                internalApiKey.getBytes(StandardCharsets.UTF_8)
        );
    }
}
