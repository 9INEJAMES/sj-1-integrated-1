package int221.integrated1backend.entities.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
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
    @JsonIgnore
    @OneToMany(mappedBy = "board",fetch = FetchType.EAGER)
    private List<Status> statuses;

    @OneToMany(mappedBy = "board",fetch = FetchType.EAGER)
    private List<TaskV2> tasks;

}
