package int221.integrated1backend.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StatusInputDTO {
    private String boardId;
    @Size(min = 0, max = 50)
    @NotNull
    private String name;
    @Size(min = 0, max = 200)
    private String description;
    private String color;

}
