package int221.integrated1backend.services;

import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.repositories.ex.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User findByUserName(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public User findByOid(String oid) { return userRepository.findByOid(oid).orElseThrow();}

    public User findByEmail(String email) { return userRepository.findByEmail(email).orElseThrow();}

}
