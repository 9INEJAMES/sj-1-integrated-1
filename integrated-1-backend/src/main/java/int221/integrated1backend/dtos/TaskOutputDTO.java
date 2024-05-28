package int221.integrated1backend.dtos;

import lombok.Data;

@Data
public class TaskOutputDTO {
    private Integer id;
    private String title;
    private String assignees;
    private StatusOutputDTO status;

}
