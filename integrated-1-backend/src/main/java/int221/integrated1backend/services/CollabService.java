package int221.integrated1backend.services;

import int221.integrated1backend.dtos.AccessRightDTO;
import int221.integrated1backend.dtos.CollabCreateInputDTO;
import int221.integrated1backend.dtos.CollabOutputDTO;
import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.entities.in.AccessRight;
import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.entities.in.Collab;
import int221.integrated1backend.repositories.in.CollabRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CollabService {
    @Autowired
    private CollabRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;

    public CollabOutputDTO mapOutputDTO(Collab collab) {//input board must have oid!!
        CollabOutputDTO collabOutputDTO = modelMapper.map(collab, CollabOutputDTO.class);
        User user = userService.findByOid(collab.getOwnerId());
        collabOutputDTO.setName(user.getName());
        collabOutputDTO.setEmail(user.getEmail());
        collabOutputDTO.setOid(user.getOid());
        return collabOutputDTO;
    }

    public List<CollabOutputDTO> mapOutputDTO(List<Collab> source) {
        return source.stream().map(entity -> mapOutputDTO(entity)).collect(Collectors.toList());
    }

    public List<Collab> getAllCollabOfBoard(String bId) {
        return repository.findAllByBoardIdOrderByCreatedOn(bId);
    }

    public List<Collab> getAllCollabBoardByOid(String oid) {
        return repository.findAllByOwnerIdOrderByCreatedOn(oid);
    }

    public Collab getCollabOfBoard(String bId, String oId, boolean isThrowError) {
        Collab collab = repository.findByBoardIdAndOwnerId(bId, oId);
        if (isThrowError && collab == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborator id does not exists in this board !!!");
        }
        return collab;
    }

    public Boolean isCollabExist(String boardId, String userOid) {
        Boolean isCollab = repository.existsByBoardIdAndOwnerId(boardId, userOid);
        return isCollab;
    }

    @Transactional("firstTransactionManager")
    public Collab createNewCollab(Board board, CollabCreateInputDTO input) {

        if (input == null || !Objects.equals(input.getAccessRight(), "WRITE") && !Objects.equals(input.getAccessRight(), "READ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }

        User user = userService.findByEmail(input.getEmail());

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified email not found.");
        }
        if (isCollabExist(board.getId(), user.getOid())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This user is already a collaborator on this board.");
        }
        if (Objects.equals(board.getOid(), user.getOid())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The board owner cannot be added as a collaborator.");
        }

        Collab newCollab = new Collab();
        newCollab.setBoardId(board.getId());
        newCollab.setOwnerId(user.getOid());
        newCollab.setAccessRight(AccessRight.valueOf(input.getAccessRight()));
        return repository.save(newCollab);
    }

    @Transactional("firstTransactionManager")
    public Collab updateCollab(String boardId, String userId, AccessRightDTO input) {
        Collab collab = getCollabOfBoard(boardId, userId, true);
        if (input == null || !Objects.equals(input.getAccessRight(), "WRITE") && !Objects.equals(input.getAccessRight(), "READ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        collab.setAccessRight(AccessRight.valueOf(input.getAccessRight()));
        return repository.save(collab);
    }

    @Transactional("firstTransactionManager")
    public Collab deleteCollab(String boardId, String userId) {
        Collab collab = getCollabOfBoard(boardId, userId, true);

        repository.delete(collab);
        return collab;
    }

    @Transactional("firstTransactionManager")
    public void removeAllCollabOfBoard(String bid) {
        repository.deleteAllByBoardId(bid);
    }
}
