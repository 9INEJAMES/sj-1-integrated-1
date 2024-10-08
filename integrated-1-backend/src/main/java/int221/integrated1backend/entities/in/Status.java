package int221.integrated1backend.entities.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "statuses")
public class Status {
    @Positive
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusId")
    private Integer id;
    @Size(min = 0, max = 50)
    @NotNull
    @Column(name = "statusName")
    private String name;
    @Size(min = 0, max = 200)
    @Column(name = "statusDescription")
    private String description;
    @Column(name = "statusColor")
    private String color;
    //    @JsonIgnore
    @OneToMany(mappedBy = "status", fetch = FetchType.EAGER)
    private List<Task> noOfTasks;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "boardId")
    private Board board;

    //    @Column(name = "createdOn", nullable = false, updatable = false)
    //    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    //    @Column(name = "updatedOn", nullable = false)
    //    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;


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

    public Integer getNoOfTasks() {
        return this.noOfTasks == null ? 0 : (this.noOfTasks.size());
    }

    private String isStringNull(String string) {
        return string == null ? null : !string.trim().isEmpty() ? string.trim() : null;
    }

    public void setName(String name) {
        this.name = name != null ? isStringNull(name) : null;
    }

    public void setDescription(String description) {
        this.description = isStringNull(description);
    }

    public void setColor(String color) {
        this.color = isStringNull(color);
    }
}
