package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    public Status findByName(String name);
}
