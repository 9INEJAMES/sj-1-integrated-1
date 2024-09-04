package int221.integrated1backend.services;

import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.entities.in.TaskV2;
import int221.integrated1backend.repositories.in.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardRepository repository;

    @Transactional("firstTransactionManager")
    public Board getBoard(String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional("firstTransactionManager")
    public Board updateLimitTask(Board boardInput) {
        Board board = getBoard(boardInput.getId());
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
}
