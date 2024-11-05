package int221.integrated1backend.entities.in;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {
    @Positive
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskId")
    private Integer id;
    @NotNull
    @Size(min = 0, max = 100)
    @Column(name = "taskTitle")
    private String title;
    @Size(min = 0, max = 500)
    @Column(name = "taskDescription")
    private String description;
    @Size(min = 0, max = 30)
    @Column(name = "taskAssignees")
    private String assignees;

    //    @Column(name = "createdOn", nullable = false, updatable = false)
    //    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    //    @Column(name = "updatedOn", nullable = false)
    //    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;
    //    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "statusId")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "boardId")
    private Board board;

    @OneToMany(mappedBy = "task",fetch = FetchType.EAGER)
    private List<Attachment> attachments;



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

    public Board setBoard(Board board) {
        this.board = board;
        return this.board;
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

}
