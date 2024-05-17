package int221.integrated1backend.dtos;

import lombok.Data;

@Data
public class TaskInputDTO {
    private String title;
    private String description;
    private String assignees;
    private String status;

    public String getStatus() {
        return status == null ? "0" : status;
    }
}
