package int221.integrated1backend.services;


import int221.integrated1backend.dtos.StatusInputDTO;
import int221.integrated1backend.entities.LimitTask;
import int221.integrated1backend.entities.Status;
import int221.integrated1backend.repositories.LimitTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LimitTaskService {
    @Autowired
    private LimitTaskRepository repository;

    public LimitTask getLimitTask() {
        return repository.findById(1).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    @Transactional
    public LimitTask updateLimitTask(LimitTask limitTaskInput) {
        LimitTask limitTask = getLimitTask();
        limitTask.setLimit(limitTaskInput.getLimit() == null ? limitTask.getLimit() : limitTaskInput.getLimit());
        limitTask.setLimitMaximumTask(limitTaskInput.getLimitMaximumTask() == null ? limitTask.getLimitMaximumTask() : limitTaskInput.getLimitMaximumTask());
        return repository.save(limitTask);
    }
}
