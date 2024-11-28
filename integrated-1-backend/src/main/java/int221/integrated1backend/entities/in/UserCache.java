package int221.integrated1backend.entities.in;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_caches")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCache {
    @Id
    @Column(name = "oid", updatable = false, nullable = false)
    private String oid;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "email", length = 50, nullable = false)
    private String email;
}
