package int221.integrated1backend.entities.ex;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    LECTURER, STAFF, STUDENT;

    @Override
    public String getAuthority() {
        return name();
    }
}

