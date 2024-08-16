package int221.integrated1backend.entities.ex;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")

public class User {
    @Id
    private String oid;
    private String username;
    private String password;
    private String name;
    private String email;
    private String role;

}
