package int221.integrated1backend.services;

import int221.integrated1backend.dtos.BoardInputDTO;
import int221.integrated1backend.dtos.BoardOutputDTO;
import int221.integrated1backend.dtos.BoardOutputDTOwithLimit;
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
    @Autowired
    private UserCacheService userCacheService;


    public BoardOutputDTOwithLimit mapOutputDTO(Board board, String type) {//input board must have oid!!
        BoardOutputDTOwithLimit boardOutputDTO = modelMapper.map(board, BoardOutputDTOwithLimit.class);
        String name = null;
        if ("AZURE".equals(type)) {
            userCacheService.findByOid(board.getOid()).getName();
        } else {
            userService.findByOid(board.getOid()).getName();
        }
        boardOutputDTO.setOName(name);
        return boardOutputDTO;
    }

    public BoardOutputDTO mapOutputDTONoLimit(Board board, String type) {//input board must have oid!!
        BoardOutputDTO boardOutputDTO = modelMapper.map(board, BoardOutputDTO.class);
        String name = null;
        if ("AZURE".equals(type)) {
            userCacheService.findByOid(board.getOid()).getName();
        } else {
            userService.findByOid(board.getOid()).getName();
        }
        boardOutputDTO.setOName(name);
        return boardOutputDTO;
    }

    public List<BoardOutputDTOwithLimit> mapOutputDTO(List<Board> source,String type) {
        return source.stream().map(entity -> mapOutputDTO(entity,type)).collect(Collectors.toList());
    }


    @Transactional("firstTransactionManager")
    public Board getBoard(String id) {
        if (id == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board doesn't exist");

        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board doesn't exist"));
    }

    @Transactional("firstTransactionManager")
    public Board updateฺBoard(String id, BoardInputDTO boardInput) {
        Board board = getBoard(id);
        if (boardInput == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        board.setName(boardInput.getName() == null ? board.getName() : boardInput.getName());
        board.setVisibility(boardInput.getVisibility() == null ? board.getVisibility() : boardInput.getVisibility());
        board.setLimit(boardInput.getLimit() == null ? board.getLimit() : boardInput.getLimit());
        board.setLimitMaximumTask(boardInput.getLimitMaximumTask() == null ? board.getLimitMaximumTask() : boardInput.getLimitMaximumTask());
        board.setUpdatedOn(null);
        return repository.save(board);
    }

    @Transactional("firstTransactionManager")
    public Board updateฺInBoard(String id) {
        Board board = getBoard(id);
        board.setUpdatedOn(null);
        return repository.save(board);
    }

    @Transactional("firstTransactionManager")
    public Board createNewBoard(Board newBoard) {
        newBoard.setLimit(false);
        newBoard.setLimitMaximumTask(10);
        return repository.save(newBoard);
    }

    //    @Transactional("firstTransactionManager")
//    public List<Board> getBoardByOId(String oid) {
//        return repository.findAllByOid(oid);
//    }
    @Transactional("firstTransactionManager")
    public List<Board> getBoardByOId(String oid) {
        return repository.findAllByOidOrderByCreatedOn(oid);
    }

    @Transactional("firstTransactionManager")
    public Board deleteBoard(String id) {
        Board board = getBoard(id);
        repository.delete(board);
        return board;
    }
}
