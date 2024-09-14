package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    public Status findByName(String name);
    public List<Status> findAllByBoardId(String bid);
}
