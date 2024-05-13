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
    @Column(name = "statusColor")
    private String color;

    @OneToMany(mappedBy = "status")
    private List<TaskV2> tasks;

    private String isStringNull(String string) {
        return string == null ? null : !string.trim().isEmpty() ? string.trim() : null;
    }

//    public String getName() {
//        return this.name.replaceAll("\\s", "_").toUpperCase();
//    }
//
    public void setName(String name) {
        this.name = name != null ? isStringNull(name): null;
    }

    public void setDescription(String description) {
        this.description = isStringNull(description);
    }

    public void setColor(String color) {
        this.color = color == null ? "#cbd5e1" : isStringNull(color);
    }
}
