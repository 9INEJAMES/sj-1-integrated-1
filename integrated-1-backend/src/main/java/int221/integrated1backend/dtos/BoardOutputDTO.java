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
    private Owner owner;
}
