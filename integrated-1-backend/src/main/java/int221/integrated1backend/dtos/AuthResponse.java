package int221.integrated1backend.dtos;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import int221.integrated1backend.models.AuthType;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    @JsonProperty("access_token")
    private final String access_token;

    @JsonProperty("refresh_token")
    private final String refresh_token;

    private AuthType type = AuthType.LOCAL;
}
