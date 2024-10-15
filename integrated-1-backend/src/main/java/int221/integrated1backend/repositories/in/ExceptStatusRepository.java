package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Collab;
import int221.integrated1backend.entities.in.CollabId;
import int221.integrated1backend.entities.in.ExceptId;
import int221.integrated1backend.entities.in.ExceptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExceptStatusRepository extends JpaRepository<ExceptStatus, ExceptId> {

}
