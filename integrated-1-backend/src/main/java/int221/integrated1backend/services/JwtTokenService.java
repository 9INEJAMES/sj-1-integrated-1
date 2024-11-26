//package int221.integrated1backend.services;
//
//import int221.integrated1backend.entities.ex.User;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cglib.core.internal.Function;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.io.Serializable;
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class JwtTokenService implements Serializable { //เอาไว้ encypt ,decypt
//    @Value("${jwt.secret}")
//    private String ACCESS_SECRET_KEY;
//    @Value("${jwt.secret.refresh}")
//    private String REFRESH_SECRET_KEY;
//    @Value("${jwt.expiration}")
//    private long ACCESS_TOKEN_EXP;
//    @Value("${jwt.expiration.refresh}")
//    private long REFRESH_TOKEN_EXP;
//    @Value("${jwt.iss}")
//    private String ISS;
//    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//
//    @Autowired
//    UserService userService;
//
//    public String getClaimValueFromToken(String token, String claim) {
//        // Parse the JWT token to get the claims
//        Claims claims = Jwts.parser()
//                .setSigningKey(ACCESS_SECRET_KEY)
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.get(claim, String.class);
//    }
//    public String getClaimValueFromRefreshToken(String token, String claim) {
//        // Parse the JWT token to get the claims
//        Claims claims = Jwts.parser()
//                .setSigningKey(REFRESH_SECRET_KEY)
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.get(claim, String.class);
//    }
//
//    public Date getExpirationDateFromToken(String token) {
//        return getClaimFromToken(token, Claims::getExpiration);
//    }
//
//    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = getAllClaimsFromToken(token);
//        return claimsResolver.apply(claims);
//    }
//
//    public Claims getAllClaimsFromToken(String token,Key key) {
//        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
//        return claims;
//    }
//    public Claims getAllClaimsFromRefreshToken(String token) {
//        Claims claims = Jwts.parser().setSigningKey(REFRESH_SECRET_KEY).parseClaimsJws(token).getBody();
//        return claims;
//    }
//
//
//    private Boolean isTokenExpired(String token) {
//        final Date expiration = getExpirationDateFromToken(token);
//        return expiration.before(new Date());
//    }
//
//    public String generateToken(String userName) {
//        User user = userService.findByUserName(userName);
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("name", user.getName());
//        claims.put("oid", user.getOid());
//        claims.put("email", user.getEmail());
//        claims.put("role", user.getRole());
//        return doGenerateToken(claims, ACCESS_SECRET_KEY, ACCESS_TOKEN_EXP);
//    }
//
//    public String generateRefreshToken(String userName) {
//        User user = userService.findByUserName(userName);
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("oid", user.getOid());
//        return doGenerateToken(claims, REFRESH_SECRET_KEY, REFRESH_TOKEN_EXP);
//    }
//
//    private String doGenerateToken(Map<String, Object> claims, String secret, long validity) {
//        return Jwts.builder().setHeaderParam("typ", "JWT")
//                .setClaims(claims)
//                .setIssuer(ISS)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + validity))
//                .signWith(signatureAlgorithm, secret).compact();
//    }
//
//    public Boolean validateToken(String token, String oid) {
//        final String oid = getClaimValueFromToken(token, "oid");
//        return (oid.equals(user.getOid()) && !isTokenExpired(token));
//    }
//    private Key getAccessKey() {
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(ACCESS_SECRET_KEY));
//    }
//
//    // Get refresh token signing key
//    private Key getRefreshKey() {
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(REFRESH_SECRET_KEY));
//    }
//}
