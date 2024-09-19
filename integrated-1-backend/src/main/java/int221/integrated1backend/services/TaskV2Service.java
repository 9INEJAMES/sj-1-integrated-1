package int221.integrated1backend.services;

import int221.integrated1backend.dtos.TaskInputDTO;
import int221.integrated1backend.dtos.TaskOutputDTO;
import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.entities.in.Status;
import int221.integrated1backend.entities.in.TaskV2;
import int221.integrated1backend.repositories.in.StatusRepository;
import int221.integrated1backend.repositories.in.TaskV2Repository;
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
    private BoardService boardService;
    @Autowired
    private StatusRepository statusRepository;
    //String boardId
    //findAllByBoardId

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

    //เพิ่ม findAllByBoardId in repo
    public List<TaskV2> getAllTaskOfBoard(String bId) {
        return repository.findAllByBoardId(bId);
    }

    public List<TaskV2> getAllTaskOfBoard(String bId, String[] statuses, String[] sortBy, String[] direction) {
        List<TaskV2> taskV2List = new ArrayList<>();
        if (statuses != null && statuses.length > 0) {
            for (int i = 0; i < statuses.length; i++) {
                Status status = statusService.findByName(statuses[i]);
                //เพิ่ม findAllByBoardIdAndStatus in repo
                taskV2List.addAll(repository.findAllByBoardIdAndStatus(bId, status));
            }
        }
        return taskV2List;
    }


    public TaskV2 findByID(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task id " + id + " does not exists !!!"));
    }

    public TaskV2 findByIdAndAndBoardId(Integer id, String bid) {
        TaskV2 task = repository.findByIdAndAndBoardId(id, bid);
        if (task == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task id " + id + " does not exists in this board !!!");
        return task;
    }

    @Transactional("firstTransactionManager")
    public TaskV2 createNewTask(TaskInputDTO taskDTO) {
        Board board = boardService.getBoard(taskDTO.getBoardId());
        Status status = statusRepository.findById(Integer.valueOf(taskDTO.getStatus())).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status id " + taskDTO.getStatus() + " does not exists !!!"));
        if (board.getLimit() && !Objects.equals(status.getName().toLowerCase(), "no status") && !Objects.equals(status.getName().toLowerCase(), "done") && status.getNoOfTasks() >= board.getLimitMaximumTask()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CAN NOT ADD TASK MORE THAN STATUS LIMIT");
        }
        taskDTO.setBoardId(null);
        TaskV2 tmp = modelMapper.map(taskDTO, TaskV2.class);
        tmp.setStatus(status);
        tmp.setBoard(board);
        return repository.save(tmp);
    }

    @Transactional("firstTransactionManager")
    public TaskOutputDTO removeTask(Integer taskId) {
        TaskV2 task = repository.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        repository.delete(task);
        return modelMapper.map(task, TaskOutputDTO.class);
    }

    @Transactional("firstTransactionManager")
    public TaskOutputDTO removeTask(Integer taskId,String bid) {
        TaskV2 task = findByIdAndAndBoardId(taskId,bid);
        repository.delete(task);
        return modelMapper.map(task, TaskOutputDTO.class);
    }

    @Transactional("firstTransactionManager")
    public void removeAllTaskOfBoard(String bid) {
        List<TaskV2> tasks= getAllTaskOfBoard(bid);
        repository.deleteAllByBoardId(bid);
    }

    @Transactional("firstTransactionManager")
    public TaskV2 updateTask(Integer taskId, TaskInputDTO taskDTO) {
        Board board = boardService.getBoard(taskDTO.getBoardId());
        taskDTO.setBoardId(null);
        TaskV2 task = modelMapper.map(taskDTO, TaskV2.class);
        task.setId(taskId);
        task.setBoard(board);
        Status status = statusRepository.findById(Integer.valueOf(taskDTO.getStatus())).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status id " + taskDTO.getStatus() + " does not exists !!!"));
        TaskV2 existingTask = findByIdAndAndBoardId(taskId, task.getBoard().getId());

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
    public List<TaskV2> updateStatusOfTask(Integer statusId, Integer newId) {
        if (Objects.equals(statusId, newId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "destination status for task transfer must be different from current status");
        Status status = statusService.findByID(statusId);
        List<TaskV2> taskV2List = repository.findAllByStatus(status);
        Status newStatus = statusRepository.findById(newId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "the specified status for task transfer does not exist"));
        Board board = boardService.getBoard(newStatus.getBoard().getId());

        if (board.getLimit() && !Objects.equals(newStatus.getName().toLowerCase(), "no status") && !Objects.equals(newStatus.getName().toLowerCase(), "done") && newStatus.getNoOfTasks() + taskV2List.size() > board.getLimitMaximumTask()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CAN NOT MOVE ALL TASKS TO NEW STATUS BECAUSE ITS OVER LIMIT");
        }
        taskV2List.stream().map(task -> task.setStatus(newStatus)).collect(Collectors.toList());
        return taskV2List;
    }
}
