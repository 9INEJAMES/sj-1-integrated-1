package int221.integrated1backend.services;

import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.entities.in.UserCache;
import int221.integrated1backend.repositories.ex.UserRepository;
import int221.integrated1backend.services.UserCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserCacheService userCacheService;

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User findByUserName(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public User findByOid(String oid) {
        User user = userRepository.findByOid(oid).orElse(null);

        if (user == null) {
            UserCache userCache = userCacheService.findByOid(oid);
            if (userCache != null) {
                user = new User();
                user.setOid(userCache.getOid());
                user.setName(userCache.getName());
                user.setEmail(userCache.getEmail());
            }
        }
        
        return user;
    }

    public User findByEmail(String email) {
        User user = null;
        try {
            user = userRepository.findByEmail(email).orElseThrow();
            System.out.println(user);
        } catch (Exception e) {
            return null;
        }
        return user;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
