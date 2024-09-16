package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.*;
import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.entities.in.Status;
import int221.integrated1backend.entities.in.TaskV2;
import int221.integrated1backend.services.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v3/boards")
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://localhost:5173"})
public class BoardControllerV3 {
    @Autowired
    private BoardService boardService;
    @Autowired
    private TaskV2Service taskService;
    @Autowired
    private StatusService statusService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;


    private String getOidFromHeader(String header) {
        String token = header.substring(7);
        return jwtTokenUtil.getClaimValueFromToken(token, "oid");
    }

    private void permissionCheck(String oid1, String oid2) {
        if (!Objects.equals(oid1, oid2)) {//check user oid by token and compare with oid in board
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You don't have permission on this board");
        }
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllBoard(@RequestHeader("Authorization") String authorizationHeader) {
        String oid = getOidFromHeader(authorizationHeader);

        List<Board> boardList = boardService.getBoardByOId(oid);
        List<BoardOutputDTOwithLimit> boardOutputDTOList = boardService.mapOutputDTOList(boardList);
        return ResponseEntity.ok(boardOutputDTOList);
    }

    @PostMapping("")
    public ResponseEntity<Object> createBoard(@RequestHeader("Authorization") String authorizationHeader, @RequestBody BoardCreateInputDTO boardInput) {
        String oid = getOidFromHeader(authorizationHeader);

        Board board = modelMapper.map(boardInput, Board.class);
        board.setOid(oid);

        Board newBoard = boardService.createNewBoard(board);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(newBoard);

        return ResponseEntity.ok(boardOutputDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBoard(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) {
        String oid = getOidFromHeader(authorizationHeader);

        Board board = boardService.getBoard(id);
//        permissionCheck(board.getOid(), oid);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board);
        return ResponseEntity.ok(boardOutputDTO);
    }

    /////////
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBoard(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id, @RequestBody BoardInputDTO boardInput) {
        String oid = getOidFromHeader(authorizationHeader);

        Board eBoard = boardService.getBoard(id);
        permissionCheck(eBoard.getOid(), oid);
        Board board = boardService.updateฺBoard(id, boardInput);
        return ResponseEntity.ok(board);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBoard(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) {
        String oid = getOidFromHeader(authorizationHeader);

        Board eBoard = boardService.getBoard(id);
        permissionCheck(eBoard.getOid(), oid);

        Board board = boardService.deleteBoard(id);
        return ResponseEntity.ok(board);
    }

    //Task operation

    @GetMapping("/{id}/tasks")
    public ResponseEntity<Object> getTasks(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id, @RequestParam(defaultValue = "") String[] filterStatuses, @RequestParam(defaultValue = "") String[] sortBy, @RequestParam(defaultValue = "ASC") String[] sortDirection) {
        String oid = getOidFromHeader(authorizationHeader);
        Board board = boardService.getBoard(id);
//        permissionCheck(board.getOid(), oid);

        if (filterStatuses.length > 0)
            return ResponseEntity.ok(taskService.getAllTaskOfBoard(id, filterStatuses, sortBy, sortDirection));
        else {
            List<TaskV2> taskList = taskService.getAllTaskOfBoard(id);
            List<TaskOutputDTO> taskDTO = listMapper.mapList(taskList, TaskOutputDTO.class, modelMapper);
            return ResponseEntity.ok(taskDTO);
        }
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<Object> addNewTask(@RequestBody TaskInputDTO taskDTO, @RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) {
        String oid = getOidFromHeader(authorizationHeader);
        taskDTO.setBoardId(id);
        TaskV2 task = taskService.createNewTask(taskDTO);
        TaskOutputAllFieldDTO outputDTO = modelMapper.map(task, TaskOutputAllFieldDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(outputDTO);
    }

    //check task id in board is exist? do it later
    @GetMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Object> getTaskById(@PathVariable Integer taskId, @RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) {
        String oid = getOidFromHeader(authorizationHeader);
        TaskV2 task = taskService.findByIdAndAndBoardId(taskId, id);
        TaskOutputAllFieldDTO outputDTO = modelMapper.map(task, TaskOutputAllFieldDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(outputDTO);
    }

    @PutMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Object> updateTask(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader, @PathVariable Integer taskId, @RequestBody TaskInputDTO taskDTO) {
        String oid = getOidFromHeader(authorizationHeader);
        taskDTO.setBoardId(id);
        TaskV2 task = taskService.updateTask(taskId, taskDTO);
        TaskOutputAllFieldDTO outputDTO = modelMapper.map(task, TaskOutputAllFieldDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(outputDTO);
    }

    @DeleteMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Object> deleteTask(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader, @PathVariable Integer taskId) {
        TaskOutputDTO taskWithIdDTO = taskService.removeTask(taskId, id);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(taskWithIdDTO);
    }

    //all statuses (public)
    @GetMapping("/{id}/statuses")
    public ResponseEntity<Object> getAllStatus(@PathVariable String id) {
        List<Status> statusList = statusService.getAllStatusByBoardId(id);
        List<StatusOutputDTO> outputDTOList = listMapper.mapList(statusList, StatusOutputDTO.class, modelMapper);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(outputDTOList);
    }

    @PostMapping("/{id}/statuses")
    public ResponseEntity<Object> addNewStatus(@PathVariable String id, @RequestBody StatusInputDTO statusInputDTO) {
        Status status = statusService.createNewStatus(statusInputDTO,boardService.getBoard(id));
        StatusOutputDTO statusOutputDTO = modelMapper.map(status, StatusOutputDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(statusOutputDTO);
    }

    @GetMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> getStatusById(@PathVariable String id, @PathVariable Integer statusId) {
        Status status = statusService.findByID(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(modelMapper.map(status, StatusLimitOutputDTO.class));
    }

    @PutMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> updateStatus(@PathVariable String id, @PathVariable Integer statusId, @RequestBody StatusInputDTO statusDTO) {
        Status status = statusService.updateStatus(statusId, statusDTO);
        StatusOutputDTO statusOutputDTO = modelMapper.map(status, StatusOutputDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(statusOutputDTO);
    }

    @DeleteMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> deleteStatus(@PathVariable String id, @PathVariable Integer statusId) {
        Status status = statusService.removeStatus(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    @DeleteMapping("/{id}/statuses/{statusId}/{newStatusId}")
    public ResponseEntity<Object> deleteStatus(@PathVariable String id, @PathVariable Integer statusId, @PathVariable Integer newStatusId) {
        List<TaskV2> taskV2List = taskService.updateStatusOfTask(statusId, newStatusId);
        Status status = statusService.removeStatus(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    @PatchMapping("/{id}/statuses/{statusId}/maximum-task")
    public ResponseEntity<Object> updateMaximumTask(@PathVariable String id, @PathVariable Integer statusId, @RequestBody StatusInputDTO statusDTO) {
        Status status = statusService.findByID(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(modelMapper.map(status, StatusLimitOutputDTO.class));
    }
}

