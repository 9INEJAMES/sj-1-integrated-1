package int221.integrated1backend.dtos;

import lombok.Data;

@Data
public class TaskDTO {
    private String title;
    private String description;
    private String assignees;
    private String status;
}
