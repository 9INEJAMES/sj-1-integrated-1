package int221.integrated1backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import int221.integrated1backend.entities.in.Board;
import lombok.Data;

@Data
public class StatusLimitOutputDTO {
    private Integer id;
    private String name;
    private String description;
    private String color;
    private Board board;
    private Integer noOfTasks;

    public String getBid(){
        return board.getId();
    }

    public boolean getLimitMaximumTask() {
        return board != null ? board.getLimit() : false;
    }
}
