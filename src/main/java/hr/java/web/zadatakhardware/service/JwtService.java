package hr.java.web.zadatakhardware.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final Key signingKey;
    private final long accessTokenValiditySeconds;

    public JwtService(
            @Value("${jwt.base64-secret}") String secret,
            @Value("${jwt.access-token-validity-seconds}") long validitySeconds) {

        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenValiditySeconds = validitySeconds;
    }

    public String generateToken(String username) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer("hardware-web-shop")
                .setSubject(username)
                .claim("type", "access")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(
                        now.plusSeconds(accessTokenValiditySeconds)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        Claims claims = extractAllClaims(token);

        return userDetails.getUsername().equals(claims.getSubject())
                && claims.getExpiration() != null
                && claims.getExpiration().after(new Date())
                && userDetails.isEnabled()
                && userDetails.isAccountNonLocked()
                && userDetails.isAccountNonExpired();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .requireIssuer("hardware-web-shop")
                .require("type", "access")
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}