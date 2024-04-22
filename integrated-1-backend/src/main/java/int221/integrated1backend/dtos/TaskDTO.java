package int221.integrated1backend.dtos;

import lombok.Data;

@Data
public class TaskDTO {
    private String taskTitle;
    private String taskDescription;
    private String taskAssignees;
    private String taskStatus;
}
