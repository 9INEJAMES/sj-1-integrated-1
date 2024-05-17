package int221.integrated1backend.services;

import int221.integrated1backend.dtos.TaskInputDTO;
import int221.integrated1backend.dtos.TaskOutputDTO;
import int221.integrated1backend.entities.LimitTask;
import int221.integrated1backend.entities.Status;
import int221.integrated1backend.entities.TaskV2;
import int221.integrated1backend.repositories.LimitTaskRepository;
import int221.integrated1backend.repositories.TaskV2Repository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service

public class TaskV2Service {
    @Autowired
    private TaskV2Repository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private StatusService statusService;
    @Autowired
    private LimitTaskRepository limitRepository;

    public List<TaskV2> getAllTask() {
        return repository.findAll();
    }

    public List<TaskV2> getAllTask(String[] statuses, String[] sortBy, String[] direction) {
        List<TaskV2> taskV2List = new ArrayList<>();
        if (statuses != null && statuses.length > 0) {
            for (int i = 0; i < statuses.length; i++) {
                Status status = statusService.findByName(statuses[i]);
                taskV2List.addAll(repository.findAllByStatus(status));
            }
        }
        return taskV2List;
    }

    public TaskV2 findByID(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task id " + id + " does not exists !!!"));
    }

    @Transactional
    public TaskV2 createNewTask(TaskInputDTO taskDTO) {
        LimitTask limitTask = limitRepository.findById(1).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Status status = statusService.findByID(Integer.valueOf(taskDTO.getStatus()));
        if (limitTask.getLimit() && status.getId() != 1 && status.getId() != 4 && status.getNoOfTasks() >= limitTask.getLimitMaximumTask()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CAN NOT ADD TASK MORE THAN STATUS LIMIT");
        }
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
        LimitTask limitTask = limitRepository.findById(1).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TaskV2 task = modelMapper.map(taskDTO, TaskV2.class);
        task.setId(taskId);
        Status status = statusService.findByID(Integer.valueOf(taskDTO.getStatus()));
        if (limitTask.getLimit() && status.getId() != 1 && status.getId() != 4 && status.getNoOfTasks() >= limitTask.getLimitMaximumTask()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "THIS STATUS HAS REACHED ITS LIMIT");
        }
        TaskV2 existingTask = findByID(taskId);
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setAssignees(task.getAssignees());
        existingTask.setStatus(status);
        existingTask.setUpdatedOn(null);
        return repository.save(existingTask);
    }

    public List<TaskV2> updateStatusOfTask(Integer statusId, Integer newId) {
        LimitTask limitTask = limitRepository.findById(1).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (Objects.equals(statusId, newId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT FOUND");
        Status status = statusService.findByID(statusId);
        List<TaskV2> taskV2List = repository.findAllByStatus(status);
        Status newStatus = statusService.findByID(newId);
        if (limitTask.getLimit() && newStatus.getId() != 1 && newStatus.getId() != 4 && newStatus.getNoOfTasks() + taskV2List.size() > limitTask.getLimitMaximumTask()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CAN NOT MOVE ALL TASKS TO NEW STATUS BECAUSE ITS OVER LIMIT");
        }
        taskV2List.stream().map(task -> task.setStatus(newStatus)).collect(Collectors.toList());
        return taskV2List;
    }
}
