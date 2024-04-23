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
    private Integer Id;
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
        return formatter.format(createdOn);
    }

    public String getUpdatedOn() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
        return formatter.format(updatedOn);
    }

    public void setCreated(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setTime(timestamp.getTime());
        this.setCreatedOn(timestamp);
    }
    public void setUpdated(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setTime(timestamp.getTime());
        this.setUpdatedOn(timestamp);
    }
}
