package int221.integrated1backend.repositories;

import int221.integrated1backend.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status,Integer> {
}
