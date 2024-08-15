package int221.integrated1backend.repositories;

import int221.integrated1backend.entities.TaskV2;
import int221.integrated1backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface UserRepository extends JpaRepository<User, Integer> {
}
