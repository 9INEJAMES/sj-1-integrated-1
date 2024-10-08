package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Collab;
import int221.integrated1backend.entities.in.CollabId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollabRepository extends JpaRepository<Collab, CollabId> {
    public List<Collab> findAllByBoardId(String bid);
    public Collab findByBoardIdAndOwnerId(String bid,String oid);
    public boolean existsByBoardIdAndOwnerId(String bid, String oid);

    public List<Collab> findAllByOwnerId(String oid);

}
