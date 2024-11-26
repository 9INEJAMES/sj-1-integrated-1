package int221.integrated1backend.dtos;

import int221.integrated1backend.models.Visibility;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BoardInputDTO {
    private String name;
    private Visibility visibility;
    private Boolean limit;
    @Positive
    private Integer limitMaximumTask;

}
