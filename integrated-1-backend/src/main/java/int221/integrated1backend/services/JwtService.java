package int221.integrated1backend.services;

import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.models.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;


@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String ACCESS_SECRET_KEY;
    @Value("${jwt.secret.refresh}")
    private String REFRESH_SECRET_KEY;
    @Value("${jwt.expiration}")
    private long ACCESS_TOKEN_EXP;
    @Value("${jwt.expiration.refresh}")
    private long REFRESH_TOKEN_EXP;
    @Value("${jwt.iss}")
    private String ISS;
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;


    public String generateAccessToken(User user) {
        return createToken(user, ACCESS_TOKEN_EXP, getAccessKey());
    }

    // Generate refresh token
    public String generateRefreshToken(User user) {
        return createToken(user, REFRESH_TOKEN_EXP, getRefreshKey());
    }

    // Extract claims using a key
    private <T> T extractClaim(String token, Key key) {
        return extractAllClaims(token, key).get("oid", (Class<T>) String.class);
    }

    public String getClaimValueFromToken(String token, TokenType type, String claim) {
        // Parse the JWT token to get the claims
        Claims claims = Jwts.parser()
                .setSigningKey(Objects.equals(type, TokenType.ACCESS) ?getAccessKey():getRefreshKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.get(claim, String.class);
    }

    public Claims extractAllClaims(String token,Key key) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }
    // Check if token is expired
    private Boolean isTokenExpired(String token,Key key) {
        return extractAllClaims(token, key).getExpiration().before(new Date());

    }

    // Validate access token (checks OID)
    public boolean validateAccessToken(String token, String oid) {
        return oid.equals(extractClaim(token, getAccessKey())) && !isTokenExpired(token, getAccessKey());
    }

    // Validate refresh token (only checks expiration)
    public boolean validateRefreshToken(String token) {
        return !isTokenExpired(token, getRefreshKey());
    }

    // Create token with expiration and key
    private String createToken(User user, long expiration, Key key) {
        Map<String, Object> claims = Map.of(
                "oid", user.getOid(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        );
        return doGenerateToken(claims, user.getUsername(), expiration, key);
    }

    private String doGenerateToken(Map<String, Object> claims, String secret, long expiration,Key key) {
        return Jwts.builder().setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuer(ISS)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key).compact();
    }

    // Get access token signing key
    private Key getAccessKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(ACCESS_SECRET_KEY));
    }

    // Get refresh token signing key
    private Key getRefreshKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(REFRESH_SECRET_KEY));
    }
}
