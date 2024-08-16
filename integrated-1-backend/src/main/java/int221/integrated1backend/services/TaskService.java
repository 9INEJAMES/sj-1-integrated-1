package int221.integrated1backend.services;

import int221.integrated1backend.dtos.TaskInputDTO;
import int221.integrated1backend.dtos.TaskOutputDTO;
import int221.integrated1backend.entities.in.Task;
import int221.integrated1backend.repositories.in.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;
    @Autowired
    private ModelMapper modelMapper;


    public List<Task> getAllTask() {
        return repository.findAll();
    }

    public Task findByID(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task id " + id + " does not exists !!!"));
    }

    @Transactional("firstTransactionManager")
    public Task createNewTask(TaskInputDTO taskDTO) {
        Task tmp = modelMapper.map(taskDTO, Task.class);
        return repository.save(tmp);
    }

    @Transactional("firstTransactionManager")
    public TaskOutputDTO removeTask(Integer taskId) {
        Task task = repository.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        repository.delete(task);
        return modelMapper.map(task, TaskOutputDTO.class);
    }

    @Transactional("firstTransactionManager")
    public Task updateTask(Integer taskId, TaskInputDTO taskDTO) {
        Task task = modelMapper.map(taskDTO, Task.class);
        task.setId(taskId);

        Task existingTask = findByID(taskId);
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setAssignees(task.getAssignees());
        existingTask.setStatus(task.getStatus());
        existingTask.setUpdatedOn(null);
        return repository.save(existingTask);
    }

}
