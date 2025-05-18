package com.catalin.vibelog.security;

import com.catalin.vibelog.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for generating and validating JSON Web Tokens (JWT)
 * used in authentication and authorization.
 */
@Component
public class JwtUtil {

    /**
     * Secret key used to sign and verify JWTs.
     * Generated once per application start.
     */
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Duration in milliseconds for which the JWT is valid.
     * Default is 1 hour.
     */
    private final long jwtExpirationInMs = 3_600_000;

    /**
     * Generates a signed JWT containing the username as subject
     * and the user's role as a custom claim.
     *
     * @param user the authenticated user for whom the token is issued
     * @return a signed JWT token string
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(key)
                .compact();
    }

    /**
     * Parses the JWT and extracts the username (subject).
     *
     * @param token the JWT token string
     * @return the username stored in the token's subject claim
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Parses the JWT and extracts the role claim.
     *
     * @param token the JWT token string
     * @return the role stored in the token's "role" claim
     */
    public String getRoleFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    /**
     * Validates the JWT for correct signature and expiration.
     *
     * @param token the JWT token string
     * @return {@code true} if the token is valid and not expired; {@code false} otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Returns the configured JWT expiration time in milliseconds.
     *
     * @return token validity duration in milliseconds
     */
    public long getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }
}
