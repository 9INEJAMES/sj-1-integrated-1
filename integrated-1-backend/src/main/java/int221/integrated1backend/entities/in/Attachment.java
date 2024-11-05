package int221.integrated1backend.entities.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Getter
@Setter
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer attachmentId;


    @JsonIgnore
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "taskId", referencedColumnName = "taskId"),
            @JoinColumn(name = "boardId", referencedColumnName = "boardId")
    })
    private Task task;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer fileSize;

    private LocalDateTime uploadDate = LocalDateTime.now();
}
