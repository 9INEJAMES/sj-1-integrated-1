package int221.integrated1backend.repositories.ex;

import int221.integrated1backend.entities.ex.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUsername(String username);
    public User findByOid(String oid);
}
