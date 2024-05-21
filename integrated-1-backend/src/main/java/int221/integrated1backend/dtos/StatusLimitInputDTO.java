package int221.integrated1backend.dtos;

import int221.integrated1backend.entities.LimitTask;
import int221.integrated1backend.entities.TaskV2;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class StatusLimitInputDTO {
    @Size(min = 0, max = 50)
    @NotNull
    private String name;
    private String description;
    private String color;
    private Boolean limitMaximumTask;

}
