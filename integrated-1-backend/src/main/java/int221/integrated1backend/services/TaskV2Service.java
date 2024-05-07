package int221.integrated1backend.services;

import int221.integrated1backend.dtos.TaskInputDTO;
import int221.integrated1backend.dtos.TaskOutputDTO;
import int221.integrated1backend.entities.Status;
import int221.integrated1backend.entities.TaskV2;
import int221.integrated1backend.repositories.TaskV2Repository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service

public class TaskV2Service {
    @Autowired
    private TaskV2Repository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private StatusService statusService;

    public List<TaskV2> getAllTask() {
        return repository.findAll();
    }

    public TaskV2 findByID(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task id " + id + " does not exists !!!"));
    }

    @Transactional
    public TaskV2 createNewTask(TaskInputDTO taskDTO) {
        Status status = statusService.findByName(taskDTO.getStatus());
        TaskV2 tmp = modelMapper.map(taskDTO, TaskV2.class);
        tmp.setStatus(status);
        return repository.save(tmp);
    }

    @Transactional
    public TaskOutputDTO removeTask(Integer taskId) {
        TaskV2 task = repository.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        repository.delete(task);
        return modelMapper.map(task, TaskOutputDTO.class);
    }

    @Transactional
    public TaskV2 updateTask(Integer taskId, TaskInputDTO taskDTO) {
        TaskV2 task = modelMapper.map(taskDTO, TaskV2.class);
        task.setId(taskId);

        Status status = statusService.findByName(taskDTO.getStatus());

        TaskV2 existingTask = findByID(taskId);
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setAssignees(task.getAssignees());
        existingTask.setStatus(status);
        existingTask.setUpdatedOn(null);
        return repository.save(existingTask);
    }

}
