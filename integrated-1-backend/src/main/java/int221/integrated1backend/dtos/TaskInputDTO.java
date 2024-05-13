package int221.integrated1backend.dtos;

import lombok.Data;

@Data
public class TaskInputDTO {
    private String title;
    private String description;
    private String assignees;
    private String status;

    private String isStringNull(String string) {
        return string == null ? null : !string.trim().isEmpty() ? string.trim() : null;
    }

    public void setStatus(String status) {
        this.status = isStringNull(status);
    }
}
