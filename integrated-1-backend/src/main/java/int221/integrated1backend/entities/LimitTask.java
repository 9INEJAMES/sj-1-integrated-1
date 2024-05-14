package int221.integrated1backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "limit_task")
public class LimitTask {
    @Id
    @Column(name = "limitId")
    private Integer id;
    @Column(name = "isLimit")
    private Boolean limit;
    @Column(name = "limitMaximumTask")
    private Integer limitMaximumTask;

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
