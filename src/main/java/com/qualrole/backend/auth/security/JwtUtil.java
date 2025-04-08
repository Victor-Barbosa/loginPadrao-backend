package com.qualrole.backend.auth.security;

import com.qualrole.backend.auth.exception.JWTSignatureException;
import com.qualrole.backend.auth.exception.JWTValidationException;
import com.qualrole.backend.user.entity.SystemUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(SystemUser user) {
        return Jwts.builder()
                .setSubject(user.getSystemUserId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String systemUserId) {
        return Jwts.builder()
                .setSubject(systemUserId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JWTValidationException("Token expirado.");
        } catch (UnsupportedJwtException e) {
            throw new JWTValidationException("Token JWT não suportado.");
        } catch (MalformedJwtException e) {
            throw new JWTValidationException("Token JWT malformado.");
        } catch (SignatureException e) {
            throw new JWTSignatureException("Assinatura do token JWT é inválida.");
        }
    }

    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, String userId) {
        return (extractUserId(token).equals(userId) && !isTokenExpired(token));
    }
}