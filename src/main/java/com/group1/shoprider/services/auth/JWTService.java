package com.group1.shoprider.services.auth;

import com.group1.shoprider.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JWTService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration-time}")
    private long jwtExpirationTime;


    /**
     * Extracts the username from the provided JWT token.
     *
     * @param token The JWT token
     * @return The extracted username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Method to extract the 'user_id' claim from the token
    public Long extractUserIdFromToken(String token) {
        return extractClaim(token, claims -> claims.get("user_id", Long.class));
    }


    /**
     * Extracts a specific claim from the provided JWT token using a claims resolver.
     *
     * @param token          The JWT token
     * @param claimsResolver The function to resolve the claim
     * @param <T>            The type of the claim
     * @return The resolved claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    /**
     * Generates a JWT token for the given user.
     *
     * @param user The user for whom the token is generated
     * @return The generated JWT token
     */
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    /**
     * Generates a JWT token with additional claims for the given user.
     *
     * @param extraClaims Additional claims to include in the token
     * @param user        The user for whom the token is generated
     * @return The generated JWT token with extra claims
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            User user
    ) {
        return buildToken(
                extraClaims,
                user,
                jwtExpirationTime);
    }


    /**
     * Builds a JWT token based on the provided claims, user, and expiration time.
     *
     * @param extraClaims    Additional claims to include in the token
     * @param user           The user for whom the token is generated
     * @param expirationTime The expiration time of the token
     * @return The built JWT token
     */
    private String buildToken(Map<String, Object> extraClaims,
                              User user, long expirationTime) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .claim("roles", user.getAuthorities())
                .claim("user_id", user.getId())
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates whether a given JWT token is valid.
     *
     * @param token       The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        return (!isTokenExpired(token));
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT token to check
     * @return true if the token has expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token's claims.
     *
     * @param token The JWT token
     * @return The expiration date extracted from the token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    /**
     * Parses and extracts all claims from a JWT token.
     *
     * @param token The JWT token to parse
     * @return The extracted claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the signing key used for JWT verification.
     *
     * @return The signing key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getTokenString(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authHeader.substring(7);
    }
}
