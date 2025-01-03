package int221.integrated1backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessToken {
    @JsonProperty("access_token")
    public final String access_token;
}
