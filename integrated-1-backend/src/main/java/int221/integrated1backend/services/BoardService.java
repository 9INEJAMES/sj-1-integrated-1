package int221.integrated1backend.services;

import int221.integrated1backend.dtos.BoardInputDTO;
import int221.integrated1backend.dtos.BoardOutputDTO;
import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.repositories.in.BoardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {
    @Autowired
    private BoardRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;


    public BoardOutputDTO mapOutputDTO(Board board) {//input board must have oid!!
        BoardOutputDTO boardOutputDTO = modelMapper.map(board, BoardOutputDTO.class);
        boardOutputDTO.setOName(userService.findByOid(board.getOid()).getName());
        return boardOutputDTO;
    }

    public List<BoardOutputDTO> mapOutputDTOList(List<Board> source) {
        return source.stream().map(entity -> mapOutputDTO(entity)).collect(Collectors.toList());
    }

    @Transactional("firstTransactionManager")
    public Board getBoard(String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional("firstTransactionManager")
    public Board updateฺBoard(String id, BoardInputDTO boardInput) {
        Board board = getBoard(id);
        board.setName(boardInput.getName() == null ? board.getName() : boardInput.getName());
        board.setLimit(boardInput.getLimit() == null ? board.getLimit() : boardInput.getLimit());
        board.setLimitMaximumTask(boardInput.getLimitMaximumTask() == null ? board.getLimitMaximumTask() : boardInput.getLimitMaximumTask());
        return repository.save(board);
    }

    @Transactional("firstTransactionManager")
    public Board createNewBoard(Board newBoard) {
        return repository.save(newBoard);
    }

    @Transactional("firstTransactionManager")
    public List<Board> getBoardByOId(String oid) {
        return repository.findAllByOid(oid);
    }

    @Transactional("firstTransactionManager")
    public Board deleteBoard(String id) {
        Board board = getBoard(id);
        repository.delete(board);
        return board;
    }
}
