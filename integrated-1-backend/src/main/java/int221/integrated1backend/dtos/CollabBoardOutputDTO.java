package int221.integrated1backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import int221.integrated1backend.entities.in.AccessRight;
import int221.integrated1backend.entities.in.CollabStatus;
import int221.integrated1backend.entities.in.Visibility;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CollabBoardOutputDTO {
    private String id;
    private String name;
    private Visibility visibility;
    private Boolean limit;
    @Positive
    private Integer limitMaximumTask;
    @JsonIgnore
    private String oid;
    @JsonIgnore
    private String oName;
    private AccessRight accessRight;
    private CollabStatus status;
    public Owner getOwner() {
        Owner owner = new Owner();
        owner.setOid(this.oid);
        owner.setName(this.oName);
        return owner;
    }

}
