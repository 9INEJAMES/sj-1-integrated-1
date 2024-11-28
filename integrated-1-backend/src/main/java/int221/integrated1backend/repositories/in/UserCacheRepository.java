package int221.integrated1backend.repositories.in;


import int221.integrated1backend.entities.in.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCacheRepository extends JpaRepository<UserCache, String> {
    Optional<UserCache> findByEmail(String email);
}
