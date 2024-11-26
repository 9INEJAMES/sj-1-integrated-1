package int221.integrated1backend.services;

import int221.integrated1backend.dtos.AuthResponse;
import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.exceptions.UnauthenticatedException;
import int221.integrated1backend.models.AuthType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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


    public AuthResponse exchangeAuthorizationCodeForToken(String authorizationCode) throws UnauthenticatedException {
        try {
            String tokenEndpoint = String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token", AZURE_TENANT_ID);

            String requestBody = buildTokenRequestBody(authorizationCode);

            String response = sendHttpPostRequest(tokenEndpoint, requestBody);

            String accessToken = extractValueFromJson(response, "access_token");
            String refreshToken = extractValueFromJson(response, "refresh_token");

            AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);
            authResponse.setType(AuthType.AZURE);

            return authResponse;
        } catch (IOException e) {
            throw new UnauthenticatedException("Failed to exchange authorization code for token");
        }
    }

    public AuthResponse refreshToken(String refreshToken) throws UnauthenticatedException {
        try {
            String tokenEndpoint = String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token", AZURE_TENANT_ID);

            String requestBody = String.format(
                    "grant_type=refresh_token&refresh_token=%s&client_id=%s&client_secret=%s",
                    URLEncoder.encode(refreshToken, "UTF-8"),
                    URLEncoder.encode(AZURE_CLIENT_ID, "UTF-8"),
                    URLEncoder.encode(AZURE_CLIENT_SECRET, "UTF-8")
            );

            String response = sendHttpPostRequest(tokenEndpoint, requestBody);

            String accessToken = extractValueFromJson(response, "access_token");
            refreshToken = extractValueFromJson(response, "refresh_token");

            AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);
            authResponse.setType(AuthType.AZURE);

            return authResponse;
        } catch (IOException e) {
            throw new UnauthenticatedException("Failed to refresh token");
        }
    }

    public User getUserFromAzureMail(String email, String token) {
        String endpoint = "https://graph.microsoft.com/v1.0/users/" + email;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/json");

            connection.setDoOutput(false);

            int responseCode = connection.getResponseCode();

            // Read the response
            BufferedReader reader;
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = reader.readLine()) != null) {
                response.append(responseLine.trim());
            }

            String jsonResponse = response.toString();
            String oid = extractValueFromJson(jsonResponse, "id");
            String mail = extractValueFromJson(jsonResponse, "mail");
            String name = extractValueFromJson(jsonResponse, "displayName");

            if (oid == null || oid.isEmpty() || mail == null || mail.isEmpty() || name == null || name.isEmpty()) {
                return null;
            }

            User user = new User();
            user.setOid(oid);
            user.setEmail(mail);
            user.setName(name);

            connection.disconnect();

            return user;
        } catch (IOException e) {
            return null;
        }
    }

    public User azureMe(String token) throws UnauthenticatedException {
        String meEndpoint = "https://graph.microsoft.com/v1.0/me";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(meEndpoint).openConnection();

            connection.setRequestMethod("GET");

            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/json");

            connection.setDoOutput(false);

            int responseCode = connection.getResponseCode();

            // Read the response
            BufferedReader reader;
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
            }

            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = reader.readLine()) != null) {
                response.append(responseLine.trim());
            }

            String jsonResponse = response.toString();
            String oid = extractValueFromJson(jsonResponse, "id");
            String email = extractValueFromJson(jsonResponse, "mail");
            String name = extractValueFromJson(jsonResponse, "displayName");

            if (oid == null || email == null || name == null) {
                throw new UnauthenticatedException("Failed to get user info from Azure");
            }

            User user = new User();
            user.setOid(oid);
            user.setEmail(email);
            user.setName(name);

            connection.disconnect();

            return user;
        } catch (IOException e) {
            throw new UnauthenticatedException("Failed to get user info from Azure");
        }
    }

    private String buildTokenRequestBody(String authorizationCode) throws UnsupportedEncodingException {
        return String.format(
                "grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s&client_secret=%s",
                URLEncoder.encode(authorizationCode, "UTF-8"),
                URLEncoder.encode(AZURE_REDIRECT_URI, "UTF-8"),
                URLEncoder.encode(AZURE_CLIENT_ID, "UTF-8"),
                URLEncoder.encode(AZURE_CLIENT_SECRET, "UTF-8")
        );
    }

    private String sendHttpPostRequest(String url, String requestBody) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes());
            os.flush();
        }

        return readResponse(connection);
    }

    private void sendHttpPostRequestWithAuth(String url, String requestBody, String token) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", "Bearer " + token);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes());
            os.flush();
        }

        readResponse(connection);
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private String extractIdTokenFromResponse(String response) {
        return extractValueFromJson(response, "id_token");
    }

    private String extractClaimFromToken(String idToken, String claimKey) {
        String[] parts = idToken.split("\\.");
        if (parts.length == 3) {
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            return extractValueFromJson(payload, claimKey);
        }
        return null;
    }

    private String extractValueFromJson(String json, String key) {
        String keyPattern = String.format("\"%s\":\"", key);
        int startIndex = json.indexOf(keyPattern) + keyPattern.length();
        int endIndex = json.indexOf("\"", startIndex);
        return startIndex >= 0 && endIndex >= 0 ? json.substring(startIndex, endIndex) : null;
    }

    public String getURL() {
        String url = "https://login.microsoftonline.com/" + AZURE_TENANT_ID +
                "/oauth2/v2.0/authorize?client_id=" + AZURE_CLIENT_ID +
                "&response_type=code" +
                "&redirect_uri=" + HOST_BASE + AZURE_REDIRECT_URI +
                "&scope=openid%20profile%20email%20offline_access%20https://graph.microsoft.com/User.ReadBasic.All";
        return url;
    }
}