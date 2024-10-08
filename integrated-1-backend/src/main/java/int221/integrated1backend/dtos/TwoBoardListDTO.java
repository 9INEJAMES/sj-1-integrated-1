package int221.integrated1backend.dtos;

import int221.integrated1backend.entities.in.Board;
import lombok.Data;

import java.util.List;

@Data
public class TwoBoardListDTO {
   private List<BoardOutputDTOwithLimit> person_boards;
   private List<CollabBoardOutputDTO> collab_boards;
}
