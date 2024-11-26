package int221.integrated1backend.repositories.ex;

import int221.integrated1backend.entities.ex.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findByUsername(String username);
    public Optional<User> findByOid(String oid);
    public Optional<User> findByEmail(String email);
}
