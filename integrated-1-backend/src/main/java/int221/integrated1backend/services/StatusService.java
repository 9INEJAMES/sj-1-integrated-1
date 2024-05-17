package int221.integrated1backend.services;

import int221.integrated1backend.dtos.StatusInputDTO;
import int221.integrated1backend.dtos.TaskOutputDTO;
import int221.integrated1backend.entities.Status;
import int221.integrated1backend.entities.TaskV2;
import int221.integrated1backend.repositories.StatusRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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


    public Status findByID(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Status id " + id + " does not exists !!!"));
    }

    public Status findByName(String name) {
        return repository.findByName(name);
    }

    private String isStringNull(String string) {
        return string == null ? null : !string.trim().isEmpty() ? string.trim() : null;
    }


    @Transactional
    public Status createNewStatus(StatusInputDTO statusInputDTO) {
        Status status = modelMapper.map(statusInputDTO, Status.class);
        return repository.save(status);
    }

    @Transactional
    public Status updateStatus(Integer id, StatusInputDTO status) {
        if (id == 1 || id == 4)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CAN'T CHANGE DEFAULT STATUS");
        Status existStatus = findByID(id);
        existStatus.setName(isStringNull(status.getName()));
        existStatus.setDescription(isStringNull(status.getDescription()));
        existStatus.setColor(isStringNull(status.getColor()));
        return repository.save(existStatus);
    }

    @Transactional
    public Status removeStatus(Integer id) {
        if (id == 1 || id == 4)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CAN'T DELETE DEFAULT STATUS");
        Status status = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        repository.delete(status);
        return status;
    }

}
