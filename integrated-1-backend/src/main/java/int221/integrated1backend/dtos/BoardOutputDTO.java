package int221.integrated1backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import int221.integrated1backend.models.Visibility;
import lombok.Data;

@Data
public class BoardOutputDTO {
    private String id;
    private String name;

    @JsonIgnore
    private String oid;
    @JsonIgnore
    private String oName;
    private Visibility visibility;

    public Owner getOwner() {
        Owner owner = new Owner();
        owner.setOid(this.oid);
        owner.setName(this.oName);
        return owner;
    }

}
