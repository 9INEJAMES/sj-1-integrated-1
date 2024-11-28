package int221.integrated1backend.services;

import int221.integrated1backend.entities.in.UserCache;
import int221.integrated1backend.repositories.in.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserCacheRepository userCacheRepository;

    @Transactional
    public UserCache save(UserCache userCache) {
        return userCacheRepository.save(userCache);
    }

    @Transactional
    public UserCache delete(UserCache userCache) {
        userCacheRepository.delete(userCache);
        return userCache;
    }
}
