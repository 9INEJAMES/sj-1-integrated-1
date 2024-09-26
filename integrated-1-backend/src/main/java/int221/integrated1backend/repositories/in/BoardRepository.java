package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.entities.in.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {

    public List<Board> findAllByOid(String oid);
    public List<Board> findAllByOidOrVisibility(String oid, Visibility visibility);
    public List<Board> findDistinctByOidAndVisibility(String oid,Visibility visibility);

}
