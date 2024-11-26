package int221.integrated1backend.entities.ex;

import int221.integrated1backend.models.AuthType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "oid", updatable = false, nullable = false)
    private String oid;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.STUDENT;

    @Transient
    private AuthType authType = AuthType.LOCAL;
    @Transient
    private String accessToken;

    public void setName(String name) {
        validateField(name, "Name", 100);
        this.name = name.trim();
    }

    public void setUsername(String username) {
        validateField(username, "Username", 50);
        this.username = username.trim();
    }

    public void setEmail(String email) {
        validateField(email, "Email", 50);
        this.email = email;
    }

    public void setPassword(String password) {
        validateField(password, "Password", 100);
        this.password = password;
    }

    private void validateField(String value, String fieldName, int maxLength) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot be longer than " + maxLength + " characters");
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
