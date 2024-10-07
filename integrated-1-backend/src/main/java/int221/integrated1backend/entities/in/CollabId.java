package int221.integrated1backend.entities.in;

import java.io.Serializable;
import java.util.Objects;

public class CollabId implements Serializable {
    private String boardId;
    private String ownerId;

    // Constructors
    public CollabId() {}

    public CollabId(String boardId, String ownerId) {
        this.boardId = boardId;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollabId collabId = (CollabId) o;
        return Objects.equals(boardId, collabId.boardId) &&
                Objects.equals(ownerId, collabId.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, ownerId);
    }
}
