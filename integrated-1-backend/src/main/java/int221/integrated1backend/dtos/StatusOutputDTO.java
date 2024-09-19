package int221.integrated1backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import int221.integrated1backend.entities.in.Board;
import lombok.Data;

@Data

public class StatusOutputDTO {
    private Integer id;
    private String name;
    private String description;
    private String color;
    private Integer noOfTasks;
    @JsonIgnore
    private Board board;

    public String getBid(){
        return board.getId();
    }
}
