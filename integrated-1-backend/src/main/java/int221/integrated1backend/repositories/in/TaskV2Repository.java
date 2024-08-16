package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Status;
import int221.integrated1backend.entities.in.TaskV2;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskV2Repository extends JpaRepository<TaskV2, Integer> {
    public List<TaskV2> findAllByStatus(Status status);

    public List<TaskV2> findAllByStatus(Status[] status, Sort sort);

}
