package int221.integrated1backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import int221.integrated1backend.entities.Status;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class TaskOutputDTO {
    private Integer id;
    private String title;
    private String assignees;
    private StatusOutputDTO status;

}
