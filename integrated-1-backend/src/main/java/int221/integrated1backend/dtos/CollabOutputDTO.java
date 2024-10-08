package int221.integrated1backend.dtos;

import int221.integrated1backend.entities.in.AccessRight;
import lombok.Data;

@Data
public class CollabOutputDTO {
    private String oid;
    private String name;
    private String email;
    private AccessRight accessRight;
    private String createdOn;
    private String updatedOn;
}
