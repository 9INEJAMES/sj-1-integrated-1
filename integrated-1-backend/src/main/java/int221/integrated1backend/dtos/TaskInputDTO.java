package int221.integrated1backend.dtos;

import int221.integrated1backend.entities.in.Attachment;
import int221.integrated1backend.utils.Constant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TaskInputDTO {
    @NotNull
    @Size(min = 0, max = Constant.TASK_TITLE_LIMIT)
    private String title;
    @Size(min = 0, max = Constant.TASK_DESCRIPTION_LIMIT)
    private String description;
    @Size(min = 0, max = Constant.TASK_ASSIGNEES_LIMIT)
    private String assignees;
    private String status;
    private String boardId;
    private List<Attachment> attachments;

    public String getStatus() {
        return status == null ? "1" : status;
    }
}
