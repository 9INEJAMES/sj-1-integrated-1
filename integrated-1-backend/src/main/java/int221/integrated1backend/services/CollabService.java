package int221.integrated1backend.services;

import int221.integrated1backend.dtos.CollabCreateInputDTO;
import int221.integrated1backend.dtos.CollabOutputDTO;
import int221.integrated1backend.entities.ex.User;
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

    public List<CollabOutputDTO> mapOutputDTOList(List<Collab> source) {
        return source.stream().map(entity -> mapOutputDTO(entity)).collect(Collectors.toList());
    }

    public List<CollabOutputDTO> getAllCollabOfBoard(String bId) {
        ;
        return mapOutputDTOList(repository.findAllByBoardId(bId));
    }

    public CollabOutputDTO getCollabOfBoard(String bId, String oId) {
        CollabOutputDTO collab = mapOutputDTO(repository.findByBoardIdAndOwnerId(bId, oId));
        if (collab == null) {
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
        User user = userService.findByEmail(input.getEmail());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with specified email not found.");
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
        newCollab.setAccessRight(input.getAccess_right());
        return repository.save(newCollab);
    }


}
