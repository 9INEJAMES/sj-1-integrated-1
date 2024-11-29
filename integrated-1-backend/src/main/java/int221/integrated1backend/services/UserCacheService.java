package int221.integrated1backend.services;

import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.entities.in.UserCache;
import int221.integrated1backend.repositories.in.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCacheService implements UserCacheInterface{

    private final UserCacheRepository userCacheRepository;

    @Transactional("firstTransactionManager")
    public UserCache save(UserCache userCache) {
        return userCacheRepository.save(userCache);
    }

    @Transactional("firstTransactionManager")
    public UserCache delete(UserCache userCache) {
        userCacheRepository.delete(userCache);
        return userCache;
    }
    @Transactional("firstTransactionManager")
    public Optional<UserCache> findByEmail(String email) {
        try {
            return Optional.of(userCacheRepository.findByEmail(email).orElseThrow());
        } catch (NoSuchElementException e) {
            return Optional.empty(); // Return Optional.empty() if user not found
        } catch (Exception e) {
            // Log the exception or handle it accordingly
            e.printStackTrace();
            return Optional.empty(); // Return Optional.empty() for other errors
        }
    }

    public UserCache findByOid(String oid) {
        return userCacheRepository.findByOid(oid).orElseThrow();
    }
}
