package int221.integrated1backend.entities.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
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
@Table(name = "boards")
public class Board {
    @Id
    @Column(name = "boardId")
    private String id;
    @Column(name = "boardName")
    private String name;
    @Column(name = "ownerId")
    private String oid;
    @Setter
    @Column(name = "isLimit")
    private Boolean limit;
    @Setter
    @Positive
    @Column(name = "limitMaximumTask")
    private Integer limitMaximumTask;
    private Date createdOn;
    private Date updatedOn;
    @JsonIgnore
    @OneToMany(mappedBy = "board",fetch = FetchType.EAGER)
    private List<Status> statuses;

    @OneToMany(mappedBy = "board",fetch = FetchType.EAGER)
    private List<TaskV2> tasks;

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
