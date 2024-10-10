package int221.integrated1backend.dtos;

import int221.integrated1backend.entities.in.AccessRight;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CollabCreateInputDTO {
    @NotNull
    @Email
    @Size(min = 0, max = 50)
    private String email;
    private AccessRight accessRight;
}
