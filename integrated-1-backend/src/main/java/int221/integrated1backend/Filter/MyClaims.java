//package int221.integrated1backend.Filter;
//
//import io.jsonwebtoken.Claims;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//public class MyClaims extends HashMap<String, Object> implements Claims {
//
//    public MyClaims() {
//        super();
//    }
//
//    public MyClaims(Map<String, Object> map) {
//        super(map);
//    }
//
//    @Override
//    public String getIssuer() {
//        return get(ISSUER, String.class);
//    }
//
//    @Override
//    public Claims setIssuer(String issuer) {
//        put(ISSUER, issuer);
//        return this;
//    }
//
//    @Override
//    public String getSubject() {
//        return get(SUBJECT, String.class);
//    }
//
//    @Override
//    public Claims setSubject(String subject) {
//        put(SUBJECT, subject);
//        return this;
//    }
//
//    @Override
//    public String getAudience() {
//        return get(AUDIENCE, String.class);
//    }
//
//    @Override
//    public Claims setAudience(String audience) {
//        put(AUDIENCE, audience);
//        return this;
//    }
//
//    @Override
//    public Date getExpiration() {
//        return get(EXPIRATION, Date.class);
//    }
//
//    @Override
//    public Claims setExpiration(Date expiration) {
//        put(EXPIRATION, expiration);
//        return this;
//    }
//
//    @Override
//    public Date getNotBefore() {
//        return get(NOT_BEFORE, Date.class);
//    }
//
//    @Override
//    public Claims setNotBefore(Date notBefore) {
//        put(NOT_BEFORE, notBefore);
//        return this;
//    }
//
//    @Override
//    public Date getIssuedAt() {
//        return get(ISSUED_AT, Date.class);
//    }
//
//    @Override
//    public Claims setIssuedAt(Date issuedAt) {
//        put(ISSUED_AT, issuedAt);
//        return this;
//    }
//
//    @Override
//    public String getId() {
//        return get(ID, String.class);
//    }
//
//    @Override
//    public Claims setId(String id) {
//        put(ID, id);
//        return this;
//    }
//
//    @Override
//    public <T> T get(String key, Class<T> requiredType) {
//        Object value = get(key);
//        if (value == null) {
//            return null;
//        }
//        if (requiredType.isInstance(value)) {
//            return requiredType.cast(value);
//        }
//        throw new IllegalArgumentException("Claim value is not of required type: " + requiredType.getName());
//    }
//
//    // Custom getters and setters for your specific claims
//
//    public String getName() {
//        return get("name", String.class);
//    }
//
//    public MyClaims setName(String name) {
//        put("name", name);
//        return this;
//    }
//
//    public String getOid() {
//        return get("oid", String.class);
//    }
//
//    public MyClaims setOid(String oid) {
//        put("oid", oid);
//        return this;
//    }
//
//    public String getEmail() {
//        return get("email", String.class);
//    }
//
//    public MyClaims setEmail(String email) {
//        put("email", email);
//        return this;
//    }
//
//    public String getRole() {
//        return get("role", String.class);
//    }
//
//    public MyClaims setRole(String role) {
//        put("role", role);
//        return this;
//    }
//}
