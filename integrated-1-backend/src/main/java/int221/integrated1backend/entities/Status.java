package int221.integrated1backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    @Size(min = 1, max = 50)
    @NotEmpty
    @Column(name = "statusName")
    private String name;
    @Size(min = 1, max = 200)
    @Column(name = "statusDescription")
    private String description;
    @Column(name = "statusColor")
    private String color;
    //    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<TaskV2> noOfTasks;

    @ManyToOne
    @JoinColumn(name = "limitId")
    private LimitTask limitMaximumTask;

    public Integer getNoOfTasks() {
        return this.noOfTasks == null ? 0 : this.noOfTasks.size();
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
