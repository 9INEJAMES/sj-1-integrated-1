package int221.integrated1backend.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskInputDTO {
    @NotNull
    @Size(min = 0, max = 100)
    private String title;
    @Size(min = 0, max = 500)
    private String description;
    @Size(min = 0, max = 30)
    private String assignees;
    private String status;

    public String getStatus() {
        return status == null ? "0" : status;
    }
}
