package int221.integrated1backend.dtos;

import lombok.Data;

@Data
public class TaskWithIdDTO {
    private Integer id;
    private String title;
    private String assignees;
    private String status;
}
