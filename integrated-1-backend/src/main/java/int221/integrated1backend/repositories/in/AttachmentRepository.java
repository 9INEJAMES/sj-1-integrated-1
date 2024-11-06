package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Attachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    Optional<Attachment> findByLocationAndTaskId(String location, Integer taskId);

}
