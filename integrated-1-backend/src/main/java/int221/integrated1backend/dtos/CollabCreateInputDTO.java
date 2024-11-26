package int221.integrated1backend.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CollabCreateInputDTO {
//    @NotNull
//    @Email
    @Size(min = 0, max = 50)
    private String email;
    private String accessRight;
}
