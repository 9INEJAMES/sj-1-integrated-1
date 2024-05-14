package int221.integrated1backend.repositories;

import int221.integrated1backend.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
