package int221.integrated1backend.dtos;

import lombok.Data;

@Data

public class StatusOutputDTO {
    private Integer id;
    private String name;
    private String description;
    private String color;
    private Integer noOfTasks;
}
