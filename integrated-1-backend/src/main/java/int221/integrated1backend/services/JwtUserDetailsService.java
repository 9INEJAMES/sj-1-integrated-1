package int221.integrated1backend.services;

import int221.integrated1backend.entities.ex.AuthUser;
import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.repositories.ex.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public User loadUserByOid(String oid) throws UsernameNotFoundException {
        return userRepository.findByOid(oid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with oid: " + oid));
    }
}