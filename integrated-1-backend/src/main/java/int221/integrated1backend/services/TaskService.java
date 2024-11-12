package int221.integrated1backend.services;

import int221.integrated1backend.dtos.TaskInputDTO;
import int221.integrated1backend.dtos.TaskOutputDTO;
import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.entities.in.Status;
import int221.integrated1backend.entities.in.Task;
import int221.integrated1backend.repositories.in.StatusRepository;
import int221.integrated1backend.repositories.in.TaskRepository;
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

public class TaskService {
    @Autowired
    private TaskRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private StatusService statusService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private StatusRepository statusRepository;

    public List<Task> getAllTask() {
        return repository.findAll();
    }

    public List<Task> getAllTask(String[] statuses) {
        List<Task> TaskList = new ArrayList<>();
        if (statuses != null && statuses.length > 0) {
            for (int i = 0; i < statuses.length; i++) {
                Status status = statusService.findByName(statuses[i]);
                TaskList.addAll(repository.findAllByStatus(status));
            }
        }
        return TaskList;
    }

    //เพิ่ม findAllByBoardId in repo
    public List<Task> getAllTaskOfBoard(String bId) {
        return repository.findAllByBoardId(bId);
    }

    public List<Task> getAllTaskOfBoard(String bId, String[] statuses) {
        List<Task> TaskList = new ArrayList<>();
        if (statuses != null && statuses.length > 0) {
            for (int i = 0; i < statuses.length; i++) {
                Status status = statusService.findByName(statuses[i]);
                //เพิ่ม findAllByBoardIdAndStatus in repo
                TaskList.addAll(repository.findAllByBoardIdAndStatus(bId, status));
            }
        }
        return TaskList;
    }


    public Task findByID(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task id " + id + " does not exists !!!"));
    }

    public Task findByIdAndAndBoardId(Integer id, String bid) {
        Task task = repository.findByIdAndAndBoardId(id, bid);
        if (task == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task id " + id + " does not exists in this board !!!");
        return task;
    }

    @Transactional("firstTransactionManager")
    public Task createNewTask(TaskInputDTO taskDTO, Board board) {
        Status status = statusRepository.findById(Integer.valueOf(taskDTO.getStatus())).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status id " + taskDTO.getStatus() + " does not exists !!!"));
        if (board.getLimit() && !Objects.equals(status.getName().toLowerCase(), "no status") && !Objects.equals(status.getName().toLowerCase(), "done") && status.getNoOfTasks() >= board.getLimitMaximumTask()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CAN NOT ADD TASK MORE THAN STATUS LIMIT");
        }
        taskDTO.setBoardId(null);
        Task tmp = modelMapper.map(taskDTO, Task.class);
        tmp.setStatus(status);
        tmp.setBoard(board);
        return repository.save(tmp);
    }

    @Transactional("firstTransactionManager")
    public TaskOutputDTO removeTask(Integer taskId) {
        Task task = repository.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        repository.delete(task);
        return modelMapper.map(task, TaskOutputDTO.class);
    }

    @Transactional("firstTransactionManager")
    public TaskOutputDTO removeTask(Integer taskId, String bid) {
        Task task = findByIdAndAndBoardId(taskId, bid);
        repository.delete(task);
        return modelMapper.map(task, TaskOutputDTO.class);
    }

    @Transactional("firstTransactionManager")
    public void removeAllTaskOfBoard(String bid) {
        repository.deleteAllByBoardId(bid);
    }

    @Transactional("firstTransactionManager")
    public Task updateTask(Integer taskId, TaskInputDTO input, String bid) {
        Board board = boardService.getBoard(bid);
        Task existingTask = findByIdAndAndBoardId(taskId, bid);

        if (input == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        input.setBoardId(null);
        Task task = modelMapper.map(input, Task.class);
        task.setId(taskId);
        task.setBoard(board);
        Status status = statusRepository.findById(Integer.valueOf(input.getStatus())).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status id " + input.getStatus() + " does not exists !!!"));

        if (!Objects.equals(status.getId(), existingTask.getStatus().getId()) && existingTask.getBoard().getLimit() && !Objects.equals(status.getName().toLowerCase(), "no status") && !Objects.equals(status.getName().toLowerCase(), "done") && status.getNoOfTasks() >= existingTask.getBoard().getLimitMaximumTask()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "THIS STATUS HAS REACHED ITS LIMIT");
        }
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setAssignees(task.getAssignees());
        existingTask.setStatus(status);
        existingTask.setUpdatedOn(null);
        return repository.save(existingTask);
    }


    @Transactional("firstTransactionManager")
    public List<Task> updateStatusOfTask(Integer statusId, Integer newId) {
        if (Objects.equals(statusId, newId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "destination status for task transfer must be different from current status");
        Status status = statusService.findByID(statusId);
        List<Task> TaskList = repository.findAllByStatus(status);
        Status newStatus = statusRepository.findById(newId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "the specified status for task transfer does not exist"));
        Board board = boardService.getBoard(newStatus.getBoard().getId());

        if (board.getLimit() && !Objects.equals(newStatus.getName().toLowerCase(), "no status") && !Objects.equals(newStatus.getName().toLowerCase(), "done") && newStatus.getNoOfTasks() + TaskList.size() > board.getLimitMaximumTask()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CAN NOT MOVE ALL TASKS TO NEW STATUS BECAUSE ITS OVER LIMIT");
        }
        TaskList.stream().map(task -> task.setStatus(newStatus)).collect(Collectors.toList());
        return TaskList;
    }
}
