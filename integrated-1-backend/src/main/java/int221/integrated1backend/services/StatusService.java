package int221.integrated1backend.services;

import int221.integrated1backend.dtos.StatusInputDTO;
import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.entities.in.Status;
import int221.integrated1backend.entities.in.TaskV2;
import int221.integrated1backend.repositories.in.StatusRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StatusService {
    @Autowired
    private StatusRepository repository;
    @Autowired
    private ModelMapper modelMapper;

    public List<Status> getAllStatus() {
        return repository.findAll();
    }
    public List<Status> getDefaultStatus() {
        return repository.findAllByBoardId("kanbanbase");
    }

    public List<Status> getAllStatusByBoardId(String id){
        List<Status> statusList = new ArrayList<>();
        statusList.addAll(getDefaultStatus());
        statusList.addAll(repository.findAllByBoardId(id));
        return statusList;
    }

    public Status findByID(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Status id " + id + " does not exists !!!"));
    }

    public Status findByName(String name) {
        return repository.findByName(name);
    }

    @Transactional("firstTransactionManager")
    public Status createNewStatus(StatusInputDTO statusInputDTO,Board board) {
        Status newStatus = modelMapper.map(statusInputDTO, Status.class);
        isUnique(newStatus);
        newStatus.setBoard(board);
        return repository.save(newStatus);
    }

    @Transactional("firstTransactionManager")
    public Status updateStatus(Integer id, StatusInputDTO statusInputDTO) {
        //มันจับ exception ของ cannot be change no status ด้านล่างก่อนจึงต้อง force check name == null
        Status existStatus = findByID(id);
        Status newStatus = modelMapper.map(statusInputDTO, Status.class);
        if (newStatus.getName() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status name must not be null");
        if (Objects.equals(existStatus.getName().toLowerCase(), "no status") || Objects.equals(existStatus.getName().toLowerCase(), "done"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status name " + existStatus.getName() + " cannot be change");
        newStatus.setId(id);
        newStatus.setBoard(existStatus.getBoard());
        isUnique(newStatus);
        return repository.save(newStatus);
    }

    //fixed later
    private void isUnique(Status newStatus) {
        List<Status> statuses = getAllStatus();
        if (newStatus.getName() != null) {
            for (Status status : statuses) {
                if (!Objects.equals(status.getId(), newStatus.getId()) && Objects.equals(status.getName().toLowerCase(), newStatus.getName().toLowerCase())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status name must be unique");
                }
            }
        }
    }

    @Transactional("firstTransactionManager")
    public Status removeStatus(Integer id) {
        Status status = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        if (Objects.equals(status.getName().toLowerCase(), "no status") || Objects.equals(status.getName().toLowerCase(), "done"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, status.getName() + " cannot be delete");
        repository.delete(status);
        return status;
    }

}
