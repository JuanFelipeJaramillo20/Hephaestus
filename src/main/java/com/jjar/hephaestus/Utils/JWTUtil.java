package com.jjar.hephaestus.Utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {

    // Base64-encoded secret key (injected from properties file)
    private static String secretKeyBase64;

    // Token expiration time (e.g., 24 hours)
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    private static SecretKey SECRET_KEY;

    /**
     * Load secret key from the properties file and initialize the `SECRET_KEY`.
     *
     * @param secretKeyBase64 Base64-encoded secret key
     */
    @Value("${jwt.secret-key-base64}")
    public void setSecretKeyBase64(String secretKeyBase64) {
        JWTUtil.secretKeyBase64 = secretKeyBase64;
        JWTUtil.SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyBase64));
    }


    /**
     * Generate a JWT token for the given email.
     *
     * @param email The email to encode in the token.
     * @return The generated JWT token.
     */
    public static String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date()) // Current time
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiry time
                .signWith(SECRET_KEY, Jwts.SIG.HS256) // Sign with secret key
                .compact(); // Compact to a string representation
    }

    /**
     * Extract email (subject) from the token.
     *
     * @param token The JWT token.
     * @return The email stored in the token.
     */
    public static String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY) // Set the key used for signing
                .build()
                .parseSignedClaims(token) // Parse the token
                .getPayload()
                .getSubject(); // Get the "subject" (email)
    }

    /**
     * Validate the token by checking email and expiration.
     *
     * @param token The JWT token.
     * @param email The email to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean validateToken(String token, String email) {
        String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    /**
     * Check if the token has expired.
     *
     * @param token The JWT token.
     * @return True if expired, false otherwise.
     */
    private static boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.before(new Date()); // Compare expiration time with current time
    }

    public static String getLoggedInUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        return extractEmail(token);
    }

}
