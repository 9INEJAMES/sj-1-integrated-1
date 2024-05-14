package int221.integrated1backend.repositories;

import int221.integrated1backend.entities.Status;
import int221.integrated1backend.entities.TaskV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskV2Repository extends JpaRepository<TaskV2, Integer> {
    public List<TaskV2> findAllByStatus(Status status);
}
