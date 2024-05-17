package int221.integrated1backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


@Getter
@Setter
@Entity
@Table(name = "tasksV2")
public class TaskV2 {
    @Positive
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskId")
    private Integer id;
    @Size(min = 1,max = 100)
    @NotEmpty
    @Column(name = "taskTitle")
    private String title;
    @Size(min = 1,max = 500)
    @Column(name = "taskDescription")
    private String description;
    @Size(min = 1,max = 30)
    @Column(name = "taskAssignees")
    private String assignees;
    //    @Column(name = "taskStatus")
//    private String status;
    private Date createdOn;
    private Date updatedOn;
    //    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "statusId")
    private Status status;


    private String isStringNull(String string) {
        return string == null ? null : !string.trim().isEmpty() ? string.trim() : null;
    }

    private String isStringNull(String string, String oldString) {
        return string == null ? oldString : !string.trim().isEmpty() ? string.trim() : oldString;
    }

    public void setTitle(String title) {
        this.title = isStringNull(title);
    }

    public void setDescription(String description) {
        this.description = isStringNull(description);
    }

    public void setAssignees(String assignees) {
        this.assignees = isStringNull(assignees);
    }

    public Status setStatus(Status status) {
        this.status = status;
        return this.status;
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
