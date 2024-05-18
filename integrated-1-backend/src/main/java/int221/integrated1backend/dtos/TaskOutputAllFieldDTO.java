package int221.integrated1backend.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class TaskOutputAllFieldDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private Date createdOn;
    private Date updatedOn;
    private StatusOutputDTO status;

}
