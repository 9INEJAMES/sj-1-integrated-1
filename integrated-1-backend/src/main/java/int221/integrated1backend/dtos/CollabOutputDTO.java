package int221.integrated1backend.dtos;

import int221.integrated1backend.entities.in.AccessRight;
import int221.integrated1backend.entities.in.CollabStatus;
import lombok.Data;

@Data
public class CollabOutputDTO {
    private String oid;
    private String name;
    private String email;
    private AccessRight accessRight;
    private CollabStatus status;
    private String createdOn;
    private String updatedOn;
}
