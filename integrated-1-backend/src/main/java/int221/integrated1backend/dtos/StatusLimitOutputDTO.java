package int221.integrated1backend.dtos;

import int221.integrated1backend.entities.in.Board;
import lombok.Data;

@Data
public class StatusLimitOutputDTO {
    private Integer id;
    private String name;
    private String description;
    private String color;
    private Board limitMaximumTask;
    private Integer noOfTasks;

    public boolean getLimitMaximumTask() {
        return limitMaximumTask != null ? limitMaximumTask.getLimit() : false;
    }
}
