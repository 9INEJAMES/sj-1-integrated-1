package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {

    public List<Board> findAllByOidOrderByCreatedOn(String oid);

}
