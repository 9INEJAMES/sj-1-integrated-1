package int221.integrated1backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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


    public String getName() {
        return this.name.replaceAll("\\s", "_").toUpperCase();
    }

    public void setName(String status) {
        this.name = status.replaceAll("\\s", "_").toUpperCase();

    }
}
