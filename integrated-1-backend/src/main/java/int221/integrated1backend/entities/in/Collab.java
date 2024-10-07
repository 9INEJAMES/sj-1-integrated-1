package int221.integrated1backend.entities.in;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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

    @Column(name = "createdOn", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updatedOn", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;
}
