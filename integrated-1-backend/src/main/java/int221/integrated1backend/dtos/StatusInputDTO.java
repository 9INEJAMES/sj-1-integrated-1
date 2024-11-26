package int221.integrated1backend.dtos;

import int221.integrated1backend.utils.Constant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StatusInputDTO {
    private String boardId;
    @Size(min = 0, max = Constant.STATUS_NAME_LIMIT)
    @NotNull
    private String name;
    @Size(min = 0, max = Constant.STATUS_DESCRIPTION_LIMIT)
    private String description;
    private String color;

}
