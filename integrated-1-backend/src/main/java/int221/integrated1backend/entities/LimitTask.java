package int221.integrated1backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Entity
@Table(name = "limit_task")
public class LimitTask {
    @Id
    @Column(name = "limitId")
    private Integer id;
    @Column(name = "isLimit")
    private Boolean limit;
    @Positive
    @Column(name = "limitMaximumTask")
    private Integer limitMaximumTask;
    @JsonIgnore
    @OneToMany(mappedBy = "limitMaximumTask")
    private List<Status> status;

    public void setId() {
        this.id = 1;
    }

    public void setLimit(Boolean limit) {
        this.limit = limit;
    }

    public void setLimitMaximumTask(Integer limitMaximumTask) {
        this.limitMaximumTask = limitMaximumTask;
    }
}
