package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Collab;
import int221.integrated1backend.entities.in.CollabId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollabRepository extends JpaRepository<Collab, CollabId> {
}
