package com.qualrole.backend.auth.security;

import com.qualrole.backend.auth.exception.JWTSignatureException;
import com.qualrole.backend.auth.exception.JWTValidationException;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String SECRET_KEY = "my-very-secure-and-super-long-secret-key-which-is-at-least-64-chars-long";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", SECRET_KEY);
        long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15;
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
        long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24;
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
    }

    @Test
    void testGenerateAccessToken() {
        SystemUser user = new SystemUser();
        ReflectionTestUtils.setField(user, "systemUserId", "1");
        user.setEmail("user@example.com");
        user.setRole(Role.STANDARD_USER);

        String token = jwtUtil.generateAccessToken(user);

        assertNotNull(token, "Token não deve ser nulo");

        Claims extractedClaims = jwtUtil.extractClaims(token);
        assertEquals("1", extractedClaims.getSubject(),
                "O subject no token deve ser igual ao userId fornecido");
        assertEquals("user@example.com", extractedClaims.get("email"),
                "A claim 'email' deve retornar o email fornecido");
        assertEquals("STANDARD_USER", extractedClaims.get("role"),
                "A claim 'role' deve retornar 'STANDARD_USER'");
    }

    @Test
    void testGenerateRefreshToken() {
        String userId = "1";

        String token = jwtUtil.generateRefreshToken(userId);

        assertNotNull(token, "Token de refresh não deve ser nulo");

        Claims extractedClaims = jwtUtil.extractClaims(token);
        assertEquals(userId, extractedClaims.getSubject(),
                "O subject no refresh token deve ser igual ao userId fornecido");
    }

    @Test
    void testExtractClaims_ValidToken() {
        SystemUser user = new SystemUser();
        ReflectionTestUtils.setField(user, "systemUserId", "1");
        user.setEmail("admin@example.com");
        user.setRole(Role.ADMIN);

        String token = jwtUtil.generateAccessToken(user);

        Claims extractedClaims = jwtUtil.extractClaims(token);

        assertNotNull(extractedClaims);
        assertEquals("1", extractedClaims.getSubject(),
                "O subject deve ser igual ao userId fornecido");
        assertEquals("admin@example.com", extractedClaims.get("email"),
                "A claim 'email' deve retornar o email fornecido");
        assertEquals("ADMIN", extractedClaims.get("role"),
                "A claim 'role' deve retornar 'ADMIN'");
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
        SystemUser user = new SystemUser();
        ReflectionTestUtils.setField(user, "systemUserId", "1");
        user.setEmail("user@example.com");
        user.setRole(Role.STANDARD_USER);

        String token = jwtUtil.generateAccessToken(user);

        boolean isValid = jwtUtil.isTokenValid(token, "1");
        assertTrue(isValid, "O token deve ser válido");
    }

    @Test
    void testIsTokenValid_InvalidUserId() {
        SystemUser user = new SystemUser();
        ReflectionTestUtils.setField(user, "systemUserId", "1");
        user.setEmail("user@example.com");
        user.setRole(Role.STANDARD_USER);

        String token = jwtUtil.generateAccessToken(user);

        boolean isValid = jwtUtil.isTokenValid(token, "other-user-id");
        assertFalse(isValid, "O token não deve ser válido para outro usuário");
    }

    @Test
    void testIsTokenValid_ExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setSubject("1")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        JWTValidationException exception = assertThrows(JWTValidationException.class, () ->
                jwtUtil.isTokenValid(expiredToken, "1")
        );

        assertEquals("Token expirado.", exception.getMessage());
    }

    @Test
    void testExtractUserId() {
        SystemUser user = new SystemUser();
        ReflectionTestUtils.setField(user, "systemUserId", "1");
        user.setEmail("user@example.com");
        user.setRole(Role.STANDARD_USER);

        String token = jwtUtil.generateAccessToken(user);

        String extractedUserId = jwtUtil.extractUserId(token);
        assertEquals("1", extractedUserId, "O userId extraído deve ser igual ao fornecido");
    }

    @Test
    void testExtractClaims_InvalidSignature() {
        SecretKey validKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(validKey, SignatureAlgorithm.HS512)
                .compact();

        SecretKey invalidKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY",
                new String(invalidKey.getEncoded(), StandardCharsets.UTF_8));

        JWTSignatureException exception = assertThrows(JWTSignatureException.class, () ->
                jwtUtil.extractClaims(token)
        );

        assertEquals("Assinatura do token JWT é inválida.", exception.getMessage());
    }
}