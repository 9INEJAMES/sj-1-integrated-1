package int221.integrated1backend.dtos;

import int221.integrated1backend.models.AccessRight;
import int221.integrated1backend.models.CollabStatus;
import lombok.Data;

@Data
public class CollabOutputDTOWithOwner {
    private String oid;
    private String name;
    private String email;
    private AccessRight accessRight;
    private CollabStatus status;
    private String createdOn;
    private String updatedOn;
    private String boardName;
    private String ownerName;
    private Integer collaborators;
}
