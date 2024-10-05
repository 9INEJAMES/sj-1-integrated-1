package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.*;
import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.entities.in.Status;
import int221.integrated1backend.entities.in.TaskV2;
import int221.integrated1backend.entities.in.Visibility;
import int221.integrated1backend.services.*;
import jakarta.validation.Valid;
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
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th", "https://ip23sj1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "https://intproj23.sit.kmutt.ac.th", "http://localhost:5173"})
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

    private void oidCheck(String oid1, String oid2) {
        if (oid2 == null || !Objects.equals(oid1, oid2)) {//check user oid by token and compare with oid in board
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission on this board");
        }
    }

    private Board permissionCheck(String authorizationHeader, String id, String method) {
        String oid = null;
        if (authorizationHeader != null) oid = getOidFromHeader(authorizationHeader);
        Board board = boardService.getBoard(id);
        if (board.getVisibility().equals(Visibility.PRIVATE)) oidCheck(board.getOid(), oid);
        if (method != "get" && board.getVisibility().equals(Visibility.PUBLIC) && oid != null)
            oidCheck(board.getOid(), oid);

        return board;
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllBoard(@RequestHeader("Authorization") String authorizationHeader) {
        String oid = getOidFromHeader(authorizationHeader);

        List<Board> boardList = boardService.getBoardByOId(oid);
        List<BoardOutputDTOwithLimit> boardOutputDTOList = boardService.mapOutputDTOList(boardList);
        return ResponseEntity.ok(boardOutputDTOList);
    }

    @PostMapping("")
    public ResponseEntity<Object> createBoard(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody(required = false) BoardCreateInputDTO boardInput) {
        String oid = getOidFromHeader(authorizationHeader);
        if (boardInput == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Board board = modelMapper.map(boardInput, Board.class);
        board.setOid(oid);

        Board newBoard = boardService.createNewBoard(board);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(newBoard);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardOutputDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBoard(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id) {

        Board board = permissionCheck(authorizationHeader, id, "get");

        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board);
        return ResponseEntity.ok(boardOutputDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBoard(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id, @Valid @RequestBody(required = false) BoardInputDTO boardInput) {
        Board existingBoard = permissionCheck(authorizationHeader, id, "put");
        if (boardInput == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Board board = boardService.updateฺBoard(id, boardInput);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board);
        return ResponseEntity.ok(boardOutputDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateVisibilityOfBoard(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id, @Valid @RequestBody(required = false) BoardInputDTO boardInput) {
        Board existingBoard = permissionCheck(authorizationHeader, id, "patch");
        if (boardInput == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Board board = boardService.updateฺBoard(id, boardInput);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board);
        return ResponseEntity.ok(boardOutputDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBoard(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) {
        Board existingBoard = permissionCheck(authorizationHeader, id, "delete");

        //delete all task and status in board!!
        taskService.removeAllTaskOfBoard(id);
        statusService.removeAllStatusOfBoard(id);

        Board board = boardService.deleteBoard(id);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board);
        return ResponseEntity.ok(boardOutputDTO);
    }

    //Task operation
    @GetMapping("/{id}/tasks")
    public ResponseEntity<Object> getTasks(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id, @RequestParam(defaultValue = "") String[] filterStatuses, @RequestParam(defaultValue = "") String[] sortBy, @RequestParam(defaultValue = "ASC") String[] sortDirection) {
        Board board = permissionCheck(authorizationHeader, id, "get");

        if (filterStatuses.length > 0)
            return ResponseEntity.ok(taskService.getAllTaskOfBoard(id, filterStatuses, sortBy, sortDirection));
        else {
            List<TaskV2> taskList = taskService.getAllTaskOfBoard(id);
            List<TaskOutputDTO> taskDTO = listMapper.mapList(taskList, TaskOutputDTO.class, modelMapper);
            return ResponseEntity.ok(taskDTO);
        }
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<Object> addNewTask(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @Valid @RequestBody(required = false) TaskInputDTO taskDTO) {
        Board board = permissionCheck(authorizationHeader, id, "post");
        if (taskDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        TaskV2 task = taskService.createNewTask(taskDTO, boardService.getBoard(id));
        TaskOutputAllFieldDTO outputDTO = modelMapper.map(task, TaskOutputAllFieldDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(outputDTO);
    }

    @GetMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Object> getTaskById(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer taskId, @PathVariable String id) {
        Board board = permissionCheck(authorizationHeader, id, "get");
        TaskV2 task = taskService.findByIdAndAndBoardId(taskId, id);
        TaskOutputAllFieldDTO outputDTO = modelMapper.map(task, TaskOutputAllFieldDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(outputDTO);
    }

    @PutMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Object> updateTask(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer taskId, @Valid @RequestBody(required = false) TaskInputDTO taskDTO) {
        Board board = permissionCheck(authorizationHeader, id, "put");
        if (taskDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        taskDTO.setBoardId(id);
        TaskV2 task = taskService.updateTask(taskId, taskDTO);
        TaskOutputAllFieldDTO outputDTO = modelMapper.map(task, TaskOutputAllFieldDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(outputDTO);
    }

    @DeleteMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Object> deleteTask(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer taskId) {
        Board board = permissionCheck(authorizationHeader, id, "delete");
        TaskOutputDTO taskWithIdDTO = taskService.removeTask(taskId, id);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(taskWithIdDTO);
    }

    @GetMapping("/{id}/statuses")
    public ResponseEntity<Object> getAllStatus(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id) {
        Board board = permissionCheck(authorizationHeader, id, "get");

        List<Status> statusList = statusService.getAllStatusByBoardId(id);
        List<StatusOutputDTO> outputDTOList = listMapper.mapList(statusList, StatusOutputDTO.class, modelMapper);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(outputDTOList);
    }

    @PostMapping("/{id}/statuses")
    public ResponseEntity<Object> addNewStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @Valid @RequestBody(required = false) StatusInputDTO statusInputDTO) {
        Board board = permissionCheck(authorizationHeader, id, "post");
        if (statusInputDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Status status = statusService.createNewStatus(statusInputDTO, boardService.getBoard(id));
        StatusOutputDTO statusOutputDTO = modelMapper.map(status, StatusOutputDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(statusOutputDTO);
    }

    @GetMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> getStatusById(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id, @PathVariable Integer statusId) {
        Board board = permissionCheck(authorizationHeader, id, "get");

        Status status = statusService.findByID(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(modelMapper.map(status, StatusLimitOutputDTO.class));
    }

    @PutMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer statusId, @Valid @RequestBody(required = false) StatusInputDTO statusDTO) {
        Board board = permissionCheck(authorizationHeader, id, "put");
        if (statusDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Status status = statusService.updateStatus(statusId, statusDTO);
        StatusOutputDTO statusOutputDTO = modelMapper.map(status, StatusOutputDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(statusOutputDTO);
    }

    @DeleteMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> deleteStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer statusId) {
        Board board = permissionCheck(authorizationHeader, id, "delete");

        Status status = statusService.removeStatus(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    @DeleteMapping("/{id}/statuses/{statusId}/{newStatusId}")
    public ResponseEntity<Object> deleteStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer statusId, @PathVariable Integer newStatusId) {
        Board board = permissionCheck(authorizationHeader, id, "delete");
        List<TaskV2> taskV2List = taskService.updateStatusOfTask(statusId, newStatusId);
        Status status = statusService.removeStatus(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    @PatchMapping("/{id}/statuses/{statusId}/maximum-task")
    public ResponseEntity<Object> updateMaximumTask(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer statusId, @Valid @RequestBody(required = false) StatusInputDTO statusDTO) {
        Board board = permissionCheck(authorizationHeader, id, "patch");
        if (statusDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Status status = statusService.findByID(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(modelMapper.map(status, StatusLimitOutputDTO.class));
    }

    //collab operation

}

