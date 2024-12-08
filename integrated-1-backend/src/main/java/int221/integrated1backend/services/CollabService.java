package int221.integrated1backend.services;

import int221.integrated1backend.dtos.AccessRightDTO;
import int221.integrated1backend.dtos.CollabCreateInputDTO;
import int221.integrated1backend.dtos.CollabOutputDTO;
import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.entities.in.Collab;
import int221.integrated1backend.entities.in.UserCache;
import int221.integrated1backend.exceptions.NotFoundException;
import int221.integrated1backend.models.AccessRight;
import int221.integrated1backend.models.AuthType;
import int221.integrated1backend.models.CollabStatus;
import int221.integrated1backend.repositories.ex.UserRepository;
import int221.integrated1backend.repositories.in.CollabRepository;
import int221.integrated1backend.repositories.in.UserCacheRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CollabService {
    @Autowired
    private CollabRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private AzureService azureService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCacheRepository userCacheRepository;
    @Autowired
    private UserCacheService userCacheService;

    public CollabOutputDTO mapOutputDTO(Collab collab) {//input board must have oid!!
        CollabOutputDTO collabOutputDTO = modelMapper.map(collab, CollabOutputDTO.class);
        
        // First try to find in users table
        User user = userRepository.findByOid(collab.getOwnerId()).orElse(null);
        
        if (user != null) {
            // If found in users table, use that data
            collabOutputDTO.setName(user.getName());
            collabOutputDTO.setEmail(user.getEmail());
            collabOutputDTO.setOid(user.getOid());
        } else {
            // If not in users table, try user_caches
            UserCache userCache = userCacheService.findByOid(collab.getOwnerId());
            if (userCache != null) {
                collabOutputDTO.setName(userCache.getName());
                collabOutputDTO.setEmail(userCache.getEmail());
                collabOutputDTO.setOid(userCache.getOid());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user does not exist");
            }
        }
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

    public Integer getNumOfCollab(String bId) {
        return repository.findAllByBoardIdAndStatusOrderByCreatedOn(bId, CollabStatus.JOINED).size();
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
    public Collab createNewCollab(Board board, CollabCreateInputDTO input, User user) {

        if (input == null || !Objects.equals(input.getAccessRight(), "WRITE") && !Objects.equals(input.getAccessRight(), "READ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified email not found.");
        }
        if (isCollabExist(board.getId(), user.getOid())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This user is already the collaborator or pending collaborator on this board.");
        }
        if (Objects.equals(board.getOid(), user.getOid())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The board owner cannot be added as a collaborator.");
        }

        Collab newCollab = new Collab();
        newCollab.setBoardId(board.getId());
        newCollab.setOwnerId(user.getOid());
        newCollab.setStatus(CollabStatus.PENDING);
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
    public Collab updateCollabStatus(String boardId, String userId, Boolean isAccept) {
        Collab collab = getCollabOfBoard(boardId, userId, true);
        if (isAccept) {
            collab.setStatus(CollabStatus.JOINED);
            repository.save(collab);
        } else {
            deleteCollab(boardId, userId);
        }
        return collab;
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

    public User findUserByEmail(String email, AuthType authType, String accessToken) {
        User user;
        if (authType == AuthType.AZURE) {
            user = azureService.getUserDetailsByEmail(email, String.valueOf(accessToken));
            if (user == null) {
                user = userService.findByEmail(email);
            }
        } else {
            user = userService.findByEmail(email);
        }

        if (user == null) {
            throw new NotFoundException("USER NOT FOUND!!");
        }

        return user;
    }
}
