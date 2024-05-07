package int221.integrated1backend.repositories;

import int221.integrated1backend.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskV2Repository extends JpaRepository<Task,Integer> {
}
