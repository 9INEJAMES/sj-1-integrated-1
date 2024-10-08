package int221.integrated1backend.entities.in;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Getter
@Setter
@Entity
@IdClass(CollabId.class)
@Table(name = "collabs")
public class Collab {
    @Id
    @Column(name = "boardId")
    private String boardId;

    @Id
    @Column(name = "ownerId")
    private String ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_right", nullable = false)
    private AccessRight accessRight = AccessRight.READ;

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
}
