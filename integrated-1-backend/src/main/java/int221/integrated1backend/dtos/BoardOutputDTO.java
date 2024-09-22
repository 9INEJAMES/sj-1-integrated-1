package int221.integrated1backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class BoardOutputDTO {
    private String id;
    private String name;

    @JsonIgnore
    private String oid;
    @JsonIgnore
    private String oName;
    @JsonIgnore
    private boolean isPublic;

    public Owner getOwner() {
        Owner owner = new Owner();
        owner.setOid(this.oid);
        owner.setName(this.oName);
        return owner;
    }

    public String getVisibility() {
        return isPublic ? "PUBLIC" : "PRIVATE";
    }
}
