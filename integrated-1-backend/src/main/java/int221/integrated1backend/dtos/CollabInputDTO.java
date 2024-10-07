package int221.integrated1backend.dtos;

import int221.integrated1backend.entities.in.AccessRight;
import lombok.Data;

@Data
public class CollabInputDTO {
    private String email;
    private AccessRight accessRight;
}
