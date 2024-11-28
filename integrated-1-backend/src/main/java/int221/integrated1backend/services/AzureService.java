package int221.integrated1backend.services;

import int221.integrated1backend.dtos.AuthResponse;
import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.entities.in.UserCache;
import int221.integrated1backend.exceptions.UnauthenticatedException;
import int221.integrated1backend.models.AuthType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AzureService {
    @Value("${azure.tenant.id}")
    private String AZURE_TENANT_ID;
    @Value("${azure.client.id}")
    private String AZURE_CLIENT_ID;
    @Value("${azure.client.secret}")
    private String AZURE_CLIENT_SECRET;
    @Value("${azure.redirect.uri}")
    private String AZURE_REDIRECT_URI;
    @Value("${web.host}")
    private String HOST_BASE;

    UserCacheService userCacheService;


    @Deprecated
    public AuthResponse exchangeAuthCodeForToken(String authorizationCode) {
        try {
            String tokenEndpoint = buildTokenEndpoint();

            String requestBody = buildAuthCodeRequestBody(authorizationCode);

            String response = sendPostRequest(tokenEndpoint, requestBody);

            String accessToken = extractJsonValue(response, "access_token");
            String refreshToken = extractJsonValue(response, "refresh_token");

            User user = fetchUserDetails(accessToken);
            if (user == null) {
                throw new UnauthenticatedException("Failed to retrieve user information from Azure.");
            }

            cacheUserDetails(user);

            return buildAuthResponse(accessToken, refreshToken, AuthType.AZURE);
        } catch (IOException e) {
            throw new UnauthenticatedException("Failed to exchange authorization code for token.");
        }
    }

    public AuthResponse refreshAccessToken(String refreshToken) {
        try {
            String tokenEndpoint = buildTokenEndpoint();

            String requestBody = buildRefreshTokenRequestBody(refreshToken);

            String response = sendPostRequest(tokenEndpoint, requestBody);

            String accessToken = extractJsonValue(response, "access_token");
            refreshToken = extractJsonValue(response, "refresh_token");

            return buildAuthResponse(accessToken, refreshToken, AuthType.AZURE);
        } catch (IOException e) {
            throw new UnauthenticatedException("Failed to refresh access token.");
        }
    }

    public User getUserDetailsByEmail(String email, String accessToken) {
        String endpoint = "https://graph.microsoft.com/v1.0/users/" + email;
        try {
            String response = sendGetRequest(endpoint, accessToken);

            String oid = extractJsonValue(response, "id");
            String mail = extractJsonValue(response, "mail");
            String name = extractJsonValue(response, "displayName");
            System.out.println(response);
            System.out.println(endpoint);
            if (isNullOrEmpty(oid) || isNullOrEmpty(mail) || isNullOrEmpty(name)) return null;

            String username = generateUsername(name);
            UserCache newUserCache = new UserCache(oid, name, username, mail);
            userCacheService.save(newUserCache);

            return buildUser(oid, name, mail);
        } catch (IOException e) {
            return null;
        }
    }

    public User fetchUserDetails(String accessToken) {
        String endpoint = "https://graph.microsoft.com/v1.0/me";
        try {
            String response = sendGetRequest(endpoint, accessToken);

            String oid = extractJsonValue(response, "id");
            String email = extractJsonValue(response, "mail");
            String name = extractJsonValue(response, "displayName");

            if (isNullOrEmpty(oid) || isNullOrEmpty(email) || isNullOrEmpty(name)) {
                throw new UnauthenticatedException("Failed to retrieve user information from Azure.");
            }

            String username = generateUsername(name);

            return buildUser(oid, name, email, username);
        } catch (IOException e) {
            throw new UnauthenticatedException("Failed to retrieve user information from Azure.");
        }
    }

    private String buildTokenEndpoint() {
        return String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token", AZURE_TENANT_ID);
    }

    private String buildAuthCodeRequestBody(String authorizationCode) throws UnsupportedEncodingException {
        return String.format(
                "grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s&client_secret=%s",
                URLEncoder.encode(authorizationCode, StandardCharsets.UTF_8),
                URLEncoder.encode(AZURE_REDIRECT_URI, StandardCharsets.UTF_8),
                URLEncoder.encode(AZURE_CLIENT_ID, StandardCharsets.UTF_8),
                URLEncoder.encode(AZURE_CLIENT_SECRET, StandardCharsets.UTF_8)
        );
    }

    private String buildRefreshTokenRequestBody(String refreshToken) throws UnsupportedEncodingException {
        return String.format(
                "grant_type=refresh_token&refresh_token=%s&client_id=%s&client_secret=%s",
                URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                URLEncoder.encode(AZURE_CLIENT_ID, StandardCharsets.UTF_8),
                URLEncoder.encode(AZURE_CLIENT_SECRET, StandardCharsets.UTF_8)
        );
    }

    private void cacheUserDetails(User user) {
        userCacheService.save(new UserCache(user.getOid(), user.getName(), user.getUsername(), user.getEmail()));
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, AuthType authType) {
        AuthResponse response = new AuthResponse(accessToken, refreshToken);
        response.setType(authType);
        return response;
    }

    private String sendPostRequest(String url, String requestBody) throws IOException {
        HttpURLConnection connection = setupHttpConnection(url, "POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        return readHttpResponse(connection);
    }

    private String sendGetRequest(String url, String token) throws IOException {
        HttpURLConnection connection = setupHttpConnection(url, "GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Accept", "application/json");

        return readHttpResponse(connection);
    }

    private HttpURLConnection setupHttpConnection(String url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput("POST".equalsIgnoreCase(method));
        return connection;
    }

    private String readHttpResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
                        ? connection.getInputStream()
                        : connection.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private String extractJsonValue(String json, String key) {
        String keyPattern = String.format("\"%s\":\"", key);
        int startIndex = json.indexOf(keyPattern) + keyPattern.length();
        int endIndex = json.indexOf("\"", startIndex);
        return startIndex >= keyPattern.length() && endIndex > startIndex ? json.substring(startIndex, endIndex) : null;
    }

    private String generateUsername(String name) {
        return "itbkk." + name.replace(" ", ".").toLowerCase();
    }

    private User buildUser(String oid, String name, String email) {
        User user = new User();
        user.setOid(oid);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private User buildUser(String oid, String name, String email, String username) {
        User user = buildUser(oid, name, email);
        user.setUsername(username);
        return user;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}