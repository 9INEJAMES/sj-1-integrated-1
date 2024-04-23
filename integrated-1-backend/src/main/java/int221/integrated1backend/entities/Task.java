package int221.integrated1backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taskId;
    private String taskTitle;
    private String taskDescription;
    private String taskAssignees;
    private String taskStatus;
    private String createdOn;
    private String updatedOn;
}
