package int221.integrated1backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;

@Data
public class BoardOutputDTO {
    private String id;
    private String name;
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
