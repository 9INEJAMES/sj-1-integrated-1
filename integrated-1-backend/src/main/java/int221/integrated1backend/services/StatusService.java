package int221.integrated1backend.services;

import int221.integrated1backend.entities.Status;
import int221.integrated1backend.repositories.StatusRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    private String isStringNull(String string) {
        return string == null ? null : !string.trim().isEmpty() ? string.trim() : null;
    }

    private String isStringNull(String string, String oldString) {
        return string == null ? oldString : !string.trim().isEmpty() ? string.trim() : oldString;
    }

    @Transactional
    public Status createNewStatus(Status status) {
        status.setName(isStringNull(status.getName()));
        status.setDescription(isStringNull(status.getDescription()));
        return repository.save(status);
    }

    @Transactional
    public Status updateStatus(Integer id, Status status) {
        Status existStatus = findByID(id);

        existStatus.setName(isStringNull(status.getName(), existStatus.getName()));
        existStatus.setDescription(isStringNull(status.getDescription(), existStatus.getDescription()));
        return repository.save(existStatus);
    }
}
