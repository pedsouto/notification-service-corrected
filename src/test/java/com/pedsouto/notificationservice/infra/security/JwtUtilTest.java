package com.pedsouto.notificationservice.infra.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {

    private static final String SECRET = "notification-service-test-secret-key-32-bytes";
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
    }

    @Test
    void shouldExtractEmailFromValidToken() {
        String token = Jwts.builder()
                .subject("user@example.com")
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertEquals("user@example.com", jwtUtil.extractUsername(token));
    }

    @Test
    void shouldRejectTokenSignedWithAnotherSecret() {
        String token = Jwts.builder()
                .subject("user@example.com")
                .signWith(Keys.hmacShaKeyFor("another-test-secret-key-with-32-bytes".getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(RuntimeException.class, () -> jwtUtil.extractUsername(token));
    }

    @Test
    void shouldRejectExpiredToken() {
        String token = Jwts.builder()
                .subject("user@example.com")
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(RuntimeException.class, () -> jwtUtil.extractUsername(token));
    }
}
