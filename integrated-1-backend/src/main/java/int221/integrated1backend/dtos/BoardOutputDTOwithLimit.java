package int221.integrated1backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import int221.integrated1backend.models.Visibility;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BoardOutputDTOwithLimit {
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

    public Owner getOwner() {
        Owner owner = new Owner();
        owner.setOid(this.oid);
        owner.setName(this.oName);
        return owner;
    }

}
