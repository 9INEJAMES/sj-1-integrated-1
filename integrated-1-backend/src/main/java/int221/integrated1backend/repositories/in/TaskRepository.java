package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
}