package int221.integrated1backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;


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
    private Date createdOn;
    private Date updatedOn;

//    public void setTitle(String title) {
//        if (title == null) this.title = null;
//        else this.title = title.trim().substring(0, 101);
//    }
//
//    public void setDescription(String description) {
//        if (description == null) this.description = null;
//        else this.description = description.trim().substring(0, 501);
//    }
//
//    public void setAssignees(String assignees) {
//        if (assignees == null) this.assignees = null;
//        else this.assignees = assignees.trim().substring(0, 31);
//    }

    public String getStatus() {
        return this.status != null ? this.status.replaceAll("\\s", "_").toUpperCase() : "NO_STATUS";
    }

    public void setStatus(String status) {
        this.status = this.status != null ? status.replaceAll("\\s", "_").toUpperCase() : "NO_STATUS";
    }

    private String getDateString(Date d) throws ParseException {
        if (d == null) d = new Date();
        SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSXXX");
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        in.setTimeZone(TimeZone.getTimeZone("UTC"));
        out.setTimeZone(TimeZone.getTimeZone("UTC"));
        return out.format(in.parse(in.format(d)));
    }

    public String getCreatedOn() throws ParseException {
        return getDateString(createdOn);
    }

    public String getUpdatedOn() throws ParseException {
        return getDateString(updatedOn);
    }
//    public void setCreatedOn() {
//        this.setCreatedOn(new Date());
//    }
//
//    public void setUpdatedOn() {
//        this.setUpdatedOn(new Date());
//    }

}
