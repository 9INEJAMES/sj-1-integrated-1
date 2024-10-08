package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Status;
import int221.integrated1backend.entities.in.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    public List<Task> findAllByStatus(Status status);
    public List<Task> findAllByStatus(Status[] status, Sort sort);
    public List<Task> findAllByBoardIdAndStatus(String id, Status status);
    public List<Task> findAllByBoardIdAndStatus(String id, Status[] status, Sort sort);
    public List<Task> findAllByBoardId(String id);
    public Task findByIdAndAndBoardId(Integer id,String bid);
    public void deleteAllByBoardId(String id);
}
