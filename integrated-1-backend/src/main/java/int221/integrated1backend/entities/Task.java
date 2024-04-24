package int221.integrated1backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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
    private String createdOn;
    private String updatedOn;
    public String getCreatedOn() {

        // Parse the timestamp string into LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(createdOn, formatter);

        // Convert LocalDateTime to OffsetDateTime with UTC offset
        OffsetDateTime offsetDateTime = localDateTime.atOffset(ZoneOffset.UTC);

        return offsetDateTime.toString();
    }

    public String getUpdatedOn() {
        // Parse the timestamp string into LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(updatedOn, formatter);

        // Convert LocalDateTime to OffsetDateTime with UTC offset
        OffsetDateTime offsetDateTime = localDateTime.atOffset(ZoneOffset.UTC);

        return offsetDateTime.toString();
    }

    public void setCreated(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setTime(timestamp.getTime());
        this.setCreatedOn(timestamp.toString());
    }
    public void setUpdated(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setTime(timestamp.getTime());
        this.setUpdatedOn(timestamp.toString());
    }
}
