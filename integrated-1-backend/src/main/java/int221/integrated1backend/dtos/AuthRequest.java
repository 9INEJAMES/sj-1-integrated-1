package int221.integrated1backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    @Size(max = 50)
    private String userName;
    @NotBlank
    @Size(max = 14)
    private String password;
}
