package int221.integrated1backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import int221.integrated1backend.entities.in.Board;
import lombok.Data;

@Data
public class TaskOutputDTO {
    private Integer id;
    private String title;
    private String assignees;
    private StatusOutputDTO status;
    @JsonIgnore
    private Board board;

    public String getBid(){
        return board.getId();
    }
}
