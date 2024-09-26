package int221.integrated1backend.dtos;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import int221.integrated1backend.entities.in.Visibility;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Setter;

@Data
public class BoardInputDTO {
    private String name;
    private Visibility visibility;
    private Boolean limit;
    @Positive
    private Integer limitMaximumTask;

}
