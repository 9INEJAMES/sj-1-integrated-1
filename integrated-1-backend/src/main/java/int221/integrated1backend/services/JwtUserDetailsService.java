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
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(userName);
//        if (user == null)
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password incorrect");//////////////exception
        List<GrantedAuthority> roles = new ArrayList<>();
        GrantedAuthority grantedAuthority = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        };
        roles.add(grantedAuthority);
        UserDetails userDetails = new AuthUser(userName, user.getPassword(), roles);
        return userDetails;
    }

    public UserDetails loadUserByOid(String oid) throws UsernameNotFoundException {
        User user = userRepository.findByOid(oid);
        List<GrantedAuthority> roles = new ArrayList<>();
        GrantedAuthority grantedAuthority = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        };
        roles.add(grantedAuthority);
        UserDetails userDetails = new AuthUser(user.getUsername(), user.getPassword(), roles);
        return userDetails;
    }
}