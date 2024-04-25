package int221.integrated1backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;


@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskId")
    private Integer id;
    @Column(name = "taskTitle")
    private String title;
    @Column(name = "taskDescription")
    private String description;
    @Column(name = "taskAssignees")
    private String assignees;
    @Column(name = "taskStatus")
    private String status;
    private Timestamp createdOn;
    private Timestamp updatedOn;

    public String getCreatedOn() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return df.format(createdOn);
    }

    public String getUpdatedOn() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return df.format(updatedOn);
    }

    public void setCreatedOn(){
        this.setCreatedOn(new Timestamp(System.currentTimeMillis()));
    }
    public void setUpdatedOn() {
        this.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
    }

}
