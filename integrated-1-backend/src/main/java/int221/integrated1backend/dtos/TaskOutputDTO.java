package int221.integrated1backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import int221.integrated1backend.entities.in.Attachment;
import int221.integrated1backend.entities.in.Board;
import lombok.Data;

import java.util.List;

@Data
public class TaskOutputDTO {
    private Integer id;
    private String title;
    private String assignees;
    private StatusOutputDTO status;
    @JsonIgnore
    private Board board;
    private List<Attachment> attachments;

    public String getBid(){
        return board.getId();
    }
}
