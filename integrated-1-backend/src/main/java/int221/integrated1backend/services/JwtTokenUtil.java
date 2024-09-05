package int221.integrated1backend.services;

import int221.integrated1backend.entities.ex.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil implements Serializable { //เอาไว้ encypt ,decypt
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("#{${jwt.max-token-interval-hour}*60*60*1000}")
    private long JWT_TOKEN_VALIDITY;
    @Value("${jwt.iss}")
    private String ISS;
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @Autowired
    UserService userService;

    public String getClaimValueFromToken(String token,String claim) {
        // Parse the JWT token to get the claims
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.get(claim, String.class);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claims;
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        User user = userService.findByUserName(userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("oid", user.getOid());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
//        claims.put("sub", user.getUsername());
        return doGenerateToken(claims);
    }

    private String doGenerateToken(Map<String, Object> claims) {
        return Jwts.builder().setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuer(ISS)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(signatureAlgorithm, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String oid = getClaimValueFromToken(token,"oid");
        User user = userService.findByUserName(userDetails.getUsername());
        return (oid.equals(user.getOid()) && !isTokenExpired(token));
    }
}
