package int221.integrated1backend.entities.in;

import java.io.Serializable;
import java.util.Objects;

public class ExceptId implements Serializable {
    private String boardId;
    private String statusId;

    // Constructors
    public ExceptId() {}

    public ExceptId(String boardId, String statusId) {
        this.boardId = boardId;
        this.statusId = statusId;
    }

    // Getters and Setters
    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setOwnerId(String statusId) {
        this.statusId = statusId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExceptId exceptId = (ExceptId) o;
        return Objects.equals(boardId, exceptId.boardId) &&
                Objects.equals(statusId, exceptId.statusId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, statusId);
    }
}
