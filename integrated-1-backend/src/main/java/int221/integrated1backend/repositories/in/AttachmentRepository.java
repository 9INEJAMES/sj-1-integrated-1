package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Attachment;

import int221.integrated1backend.entities.in.Task;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    public Optional<Attachment> findByLocationAndTaskId(String location, Integer taskId);

    @Query("SELECT SUM(a.fileSize) FROM Attachment a WHERE a.task.id = :taskId")
   public Long getTotalFileSizeByTaskId(Integer taskId);

    public List<Attachment> findAllByTask(Task task);
    public void deleteAllByTask(Task task);

}
