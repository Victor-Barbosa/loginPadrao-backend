package com.qualrole.backend.auth.security;

import com.qualrole.backend.auth.exception.JWTSignatureException;
import com.qualrole.backend.auth.exception.JWTValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String SECRET_KEY = "my-very-secure-and-super-long-secret-key-which-is-at-least-64-chars-long";
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15;
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
    }

    @Test
    void testGenerateAccessToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        String subject = "testuser";

        String token = jwtUtil.generateAccessToken(claims, subject);
        assertNotNull(token);

        Claims extractedClaims = jwtUtil.extractClaims(token);
        assertEquals(subject, extractedClaims.getSubject());
        assertEquals("USER", extractedClaims.get("role"));
    }

    @Test
    void testGenerateRefreshToken() {
        String subject = "testuser";

        String token = jwtUtil.generateRefreshToken(subject);
        assertNotNull(token);

        Claims extractedClaims = jwtUtil.extractClaims(token);
        assertEquals(subject, extractedClaims.getSubject());
    }

    @Test
    void testExtractClaims_ValidToken() {
        String subject = "testuser";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        String token = jwtUtil.generateAccessToken(claims, subject);
        Claims extractedClaims = jwtUtil.extractClaims(token);

        assertNotNull(extractedClaims);
        assertEquals(subject, extractedClaims.getSubject());
        assertEquals("ADMIN", extractedClaims.get("role"));
    }

    @Test
    void testExtractClaims_ExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        JWTValidationException exception = assertThrows(JWTValidationException.class, () ->
                jwtUtil.extractClaims(expiredToken)
        );
        assertEquals("Token expirado.", exception.getMessage());
    }

    @Test
    void testIsTokenValid_ValidToken() {
        String subject = "testuser";
        String token = jwtUtil.generateAccessToken(new HashMap<>(), subject);

        boolean isValid = jwtUtil.isTokenValid(token, subject);
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_InvalidUsername() {
        String subject = "testuser";
        String token = jwtUtil.generateAccessToken(new HashMap<>(), subject);

        boolean isValid = jwtUtil.isTokenValid(token, "otheruser");
        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_ExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        JWTValidationException exception = assertThrows(JWTValidationException.class, () ->
                jwtUtil.isTokenValid(expiredToken, "testuser")
        );
        assertEquals("Token expirado.", exception.getMessage());
    }

    @Test
    void testExtractUsername() {
        String subject = "testuser";
        String token = jwtUtil.generateAccessToken(new HashMap<>(), subject);

        String username = jwtUtil.extractUsername(token);
        assertEquals(subject, username);
    }

    @Test
    void testExtractClaims_InvalidSignature() {
        String subject = "testuser";

        SecretKey validKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(validKey, SignatureAlgorithm.HS512)
                .compact();

        SecretKey differentKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", new String(differentKey.getEncoded()));

        JWTSignatureException exception = assertThrows(JWTSignatureException.class, () ->
                jwtUtil.extractClaims(token)
        );

        assertEquals("Assinatura do token JWT é inválida.", exception.getMessage());
    }
}