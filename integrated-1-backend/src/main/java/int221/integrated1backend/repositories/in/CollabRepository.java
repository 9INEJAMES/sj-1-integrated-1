package int221.integrated1backend.repositories.in;

import int221.integrated1backend.entities.in.Collab;
import int221.integrated1backend.entities.in.CollabId;
import int221.integrated1backend.models.CollabStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollabRepository extends JpaRepository<Collab, CollabId> {
    public List<Collab> findAllByBoardIdOrderByCreatedOn(String bid);

    public Collab findByBoardIdAndOwnerId(String bid, String oid);

    public boolean existsByBoardIdAndOwnerId(String bid, String oid);
    public List<Collab> findAllByOwnerIdOrderByCreatedOn(String oid);
    public List<Collab> findAllByOwnerIdAndStatusOrderByCreatedOn(String oid, CollabStatus status);
    public void deleteAllByBoardId(String id);
    public List<Collab> findAllByBoardIdAndStatusOrderByCreatedOn(String bid,CollabStatus status);

}
