package int221.integrated1backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "statuses")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusId")
    private Integer id;
    @Column(name = "statusName")
    private String name;
    @Column(name = "statusDescription")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<TaskV2> task;

    public String getName() {
        return this.name.replaceAll("\\s", "_").toUpperCase();
    }

    public void setName(String status) {
        this.name = status.replaceAll("\\s", "_").toUpperCase();

    }
}
