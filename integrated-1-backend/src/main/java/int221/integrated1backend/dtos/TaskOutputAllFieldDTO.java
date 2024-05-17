package int221.integrated1backend.dtos;

import lombok.Data;

@Data
public class TaskOutputAllFieldDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private StatusOutputDTO status;

}
