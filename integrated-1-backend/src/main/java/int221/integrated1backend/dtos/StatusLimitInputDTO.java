package int221.integrated1backend.dtos;

import int221.integrated1backend.entities.LimitTask;
import int221.integrated1backend.entities.TaskV2;
import lombok.Data;

import java.util.List;

@Data
public class StatusLimitInputDTO {
    private String name;
    private String description;
    private String color;
    private Boolean limitMaximumTask;

}
