package int221.integrated1backend.entities.ex;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")

public class User {
    @Id
    @Column(name = "oid")
    private String oid;
    @NotEmpty(message = "Username cannot be empty")
    @Size(max = 50)
    @Column(name = "username")
    private String username;
    @NotEmpty(message = "Password cannot be empty")
    @Size(max = 14)
    @Column(name = "password")
    private String password;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "role")
    private String role;

}
