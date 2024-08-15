package int221.integrated1backend.services;

import int221.integrated1backend.entities.User;
import int221.integrated1backend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private StatusService statusService;

    public List<User> getAllUser() {
        return userRepository.findAll();
    }




}
