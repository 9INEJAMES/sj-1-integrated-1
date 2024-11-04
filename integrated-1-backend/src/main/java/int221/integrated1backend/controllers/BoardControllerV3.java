package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.*;
import int221.integrated1backend.entities.in.*;
import int221.integrated1backend.services.*;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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
    private TaskService taskService;
    @Autowired
    private StatusService statusService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CollabService collabService;
    @Autowired
    private EmailService emailService;


    private String getOidFromHeader(String header) {
        if (header == null) return null;
        String token = header.substring(7);
        return jwtTokenUtil.getClaimValueFromToken(token, "oid");
    }

    private String getNameFromHeader(String header) {
        if (header == null) return null;
        String token = header.substring(7);
        return jwtTokenUtil.getClaimValueFromToken(token, "name");
    }

    private AccessRight oidCheck(Board board, String userOid, String method, Visibility visibility, Boolean isCollabCanDoOperation) {
        boolean isOwner = Objects.equals(board.getOid(), userOid);
        Collab collab = isOwner ? null : collabService.getCollabOfBoard(board.getId(), userOid, false);

        boolean isCollab = isOwner || (collab != null && collab.getStatus() == CollabStatus.JOINED);
        boolean isWriteAccess = isOwner || (collab != null && collab.getAccessRight() == AccessRight.WRITE);
        boolean isCanDoOp = Objects.equals(method, "get") || isCollabCanDoOperation;
        if (!Objects.equals(method, "get") && !isWriteAccess && !Objects.equals(board.getId(), "kanbanbase")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission on this board");
        }
        if (!isOwner && !isCanDoOp && !Objects.equals(board.getId(), "kanbanbase")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission on this board");
        }

        if (visibility == Visibility.PRIVATE) {
            if (!isCollab && !Objects.equals(board.getId(), "kanbanbase")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission on this board");
            }
        }

        return isOwner || (collab != null && collab.getStatus() == CollabStatus.JOINED && collab.getAccessRight() == AccessRight.WRITE) ? AccessRight.WRITE : AccessRight.READ;
    }

    private Board permissionCheck(String authorizationHeader, String bid, String method, Boolean isCollabCanDoOperation) {
        String userId = null;
        if (authorizationHeader != null) userId = getOidFromHeader(authorizationHeader);
        Board board = boardService.getBoard(bid);
        if ((!Objects.equals(method, "get") && board.getVisibility().equals(Visibility.PUBLIC) && userId != null) || board.getVisibility().equals(Visibility.PRIVATE)) {
            oidCheck(board, userId, method, board.getVisibility(), isCollabCanDoOperation);
        }
        return board;
    }

    @GetMapping("/{id}/access")
    public ResponseEntity<Object> getBoardAccess(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id) {
        Board board = permissionCheck(authorizationHeader, id, "get", true);
        AccessRightDTO output = new AccessRightDTO();
        output.setAccessRight(String.valueOf(oidCheck(board, getOidFromHeader(authorizationHeader), "get", board.getVisibility(), true)));
        return ResponseEntity.ok(output);
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllBoard(@RequestHeader(value = "Authorization") String authorizationHeader) {
        String userId = getOidFromHeader(authorizationHeader);

        List<Board> boardList = boardService.getBoardByOId(userId);
        List<BoardOutputDTOwithLimit> boardOutputDTOList = boardService.mapOutputDTO(boardList);
        List<CollabBoardOutputDTO> collabBoards = new ArrayList<>();

        /////////////
        List<Collab> collabs = collabService.getAllCollabBoardByOid(userId);
        for (Collab collab : collabs) {
            CollabBoardOutputDTO collabOutputDTO = modelMapper.map(boardService.mapOutputDTO(boardService.getBoard(collab.getBoardId())), CollabBoardOutputDTO.class);
            collabOutputDTO.setAccessRight(collab.getAccessRight());
            collabBoards.add(collabOutputDTO);
        }

        TwoBoardListDTO output = new TwoBoardListDTO();
        output.setPerson_boards(boardOutputDTOList);
        output.setCollab_boards(collabBoards);

        return ResponseEntity.ok(output);
    }

    ///
    @PostMapping("")
    public ResponseEntity<Object> createBoard(@RequestHeader(value = "Authorization") String authorizationHeader, @Valid @RequestBody(required = false) BoardCreateInputDTO input) {
        String oid = getOidFromHeader(authorizationHeader);
        if (input == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Board board = modelMapper.map(input, Board.class);
        board.setOid(oid);
        Board newBoard = boardService.createNewBoard(board);
        statusService.createNewDefaultStatus(board);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(newBoard);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardOutputDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBoard(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id) {

        Board board = permissionCheck(authorizationHeader, id, "get", true);

        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board);
        return ResponseEntity.ok(boardOutputDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBoard(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @Valid @RequestBody(required = false) BoardInputDTO input) {
        Board existingBoard = permissionCheck(authorizationHeader, id, "put", false);

        Board board = boardService.updateฺBoard(id, input);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board);
        return ResponseEntity.ok(boardOutputDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateVisibilityOfBoard(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @Valid @RequestBody(required = false) BoardInputDTO input) {
        Board existingBoard = permissionCheck(authorizationHeader, id, "patch", false);
        if (input == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Board board = boardService.updateฺBoard(id, input);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board);
        return ResponseEntity.ok(boardOutputDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBoard(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id) {
        Board existingBoard = permissionCheck(authorizationHeader, id, "delete", false);

        //delete all task and status in board!!
        taskService.removeAllTaskOfBoard(id);
        statusService.removeAllStatusOfBoard(id);
        collabService.removeAllCollabOfBoard(id);

        Board board = boardService.deleteBoard(id);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board);
        return ResponseEntity.ok(boardOutputDTO);
    }

    //Task operation

    @GetMapping("/{id}/tasks")
    public ResponseEntity<Object> getTasks(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id, @RequestParam(defaultValue = "") String[] filterStatuses) {
        Board board = permissionCheck(authorizationHeader, id, "get", true);

        if (filterStatuses.length > 0) return ResponseEntity.ok(taskService.getAllTaskOfBoard(id, filterStatuses));
        else {
            List<Task> taskList = taskService.getAllTaskOfBoard(id);
            List<TaskOutputDTO> taskDTO = listMapper.mapList(taskList, TaskOutputDTO.class, modelMapper);
            return ResponseEntity.ok(taskDTO);
        }
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<Object> addNewTask(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @Valid @RequestBody(required = false) TaskInputDTO input) {
        Board board = permissionCheck(authorizationHeader, id, "post", true);
        if (input == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Task task = taskService.createNewTask(input, boardService.getBoard(id));
        TaskOutputAllFieldDTO outputDTO = modelMapper.map(task, TaskOutputAllFieldDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(outputDTO);
    }

    //check task id in board is exist? do it later
    @GetMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Object> getTaskById(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer taskId, @PathVariable String id) {
        Board board = permissionCheck(authorizationHeader, id, "get", true);
        Task task = taskService.findByIdAndAndBoardId(taskId, id);
        TaskOutputAllFieldDTO outputDTO = modelMapper.map(task, TaskOutputAllFieldDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(outputDTO);
    }


    @PutMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Object> updateTask(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer taskId, @Valid @RequestBody(required = false) TaskInputDTO input) {
        Board board = permissionCheck(authorizationHeader, id, "put", true);

        Task task = taskService.updateTask(taskId, input, id);
        TaskOutputAllFieldDTO outputDTO = modelMapper.map(task, TaskOutputAllFieldDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(outputDTO);
    }

    @DeleteMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Object> deleteTask(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer taskId) {
        Board board = permissionCheck(authorizationHeader, id, "delete", true);
        TaskOutputDTO taskWithIdDTO = taskService.removeTask(taskId, id);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(taskWithIdDTO);
    }

    //all statuses (public)
    @GetMapping("/{id}/statuses")
    public ResponseEntity<Object> getAllStatus(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id) {
        Board board = permissionCheck(authorizationHeader, id, "get", true);

        List<Status> statusList = statusService.getAllStatusByBoardId(id);
        List<StatusOutputDTO> outputDTOList = listMapper.mapList(statusList, StatusOutputDTO.class, modelMapper);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(outputDTOList);
    }

    @PostMapping("/{id}/statuses")
    public ResponseEntity<Object> addNewStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @Valid @RequestBody(required = false) StatusInputDTO input) {
        Board board = permissionCheck(authorizationHeader, id, "post", true);

        Status status = statusService.createNewStatus(input, boardService.getBoard(id));
        StatusOutputDTO statusOutputDTO = modelMapper.map(status, StatusOutputDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(statusOutputDTO);
    }

    @GetMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> getStatusById(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id, @PathVariable Integer statusId) {
        Board board = permissionCheck(authorizationHeader, id, "get", true);

        Status status = statusService.findByID(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(modelMapper.map(status, StatusLimitOutputDTO.class));
    }

    ///fixed self status
    @PutMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer statusId, @Valid @RequestBody(required = false) StatusInputDTO input) {
        Board board = permissionCheck(authorizationHeader, id, "put", true);

        Status status = statusService.updateStatus(statusId, input);
        StatusOutputDTO statusOutputDTO = modelMapper.map(status, StatusOutputDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(statusOutputDTO);
    }

    @DeleteMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> deleteStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer statusId) {
        Board board = permissionCheck(authorizationHeader, id, "delete", true);

        Status status = statusService.removeStatus(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    @DeleteMapping("/{id}/statuses/{statusId}/{newStatusId}")
    public ResponseEntity<Object> deleteStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer statusId, @PathVariable Integer newStatusId) {
        Board board = permissionCheck(authorizationHeader, id, "delete", true);
        List<Task> TaskList = taskService.updateStatusOfTask(statusId, newStatusId);
        Status status = statusService.removeStatus(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    ////

    @PatchMapping("/{id}/statuses/{statusId}/maximum-task")
    public ResponseEntity<Object> updateMaximumTask(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable Integer statusId, @Valid @RequestBody(required = false) StatusInputDTO input) {
        Board board = permissionCheck(authorizationHeader, id, "patch", true);
        if (input == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Status status = statusService.findByID(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(modelMapper.map(status, StatusLimitOutputDTO.class));
    }

    @GetMapping("/{id}/collabs")
    public ResponseEntity<Object> getCollab(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id) {
        Board board = permissionCheck(authorizationHeader, id, "get", true);

        List<CollabOutputDTO> collabs = collabService.mapOutputDTO(collabService.getAllCollabOfBoard(id));
        return ResponseEntity.ok(collabs);
    }

    @GetMapping("/{id}/collabs/{collab_oid}")
    public ResponseEntity<Object> getCollabById(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id, @PathVariable String collab_oid) {
        Board board = permissionCheck(authorizationHeader, id, "get", true);

        CollabOutputDTO collab = collabService.mapOutputDTO(collabService.getCollabOfBoard(id, collab_oid, true));
        return ResponseEntity.ok(collab);
    }
    @PostMapping("/{id}/collabs")
    public ResponseEntity<Object> createCollab(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @Valid @RequestBody(required = false) CollabCreateInputDTO input) throws MessagingException {
        Board board = permissionCheck(authorizationHeader, id,  "post", false);

        CollabOutputDTO newCollab = collabService.mapOutputDTO(collabService.createNewCollab(board, input));
        String userName = null;
        if (authorizationHeader != null) userName = getNameFromHeader(authorizationHeader);
        emailService.sendInviteEmail(input.getEmail(),userName,newCollab.getAccessRight(), board);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCollab);
    }

    @PatchMapping("/{id}/collabs/{collab_oid}")
    public ResponseEntity<Object> updateAccessRight(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable String collab_oid, @RequestBody(required = false) AccessRightDTO input) {
        Board board = permissionCheck(authorizationHeader, id, "patch", false);

        CollabOutputDTO collab = collabService.mapOutputDTO(collabService.updateCollab(id, collab_oid, input));
        return ResponseEntity.ok(collab);
    }


    @DeleteMapping("/{id}/collabs/{collab_oid}")
    public ResponseEntity<Object> deleteCollab(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable String id, @PathVariable String collab_oid) {
        String method = "delete";
        String oid = getOidFromHeader(authorizationHeader);
        if (Objects.equals(oid, collab_oid)) method = "get";

        Board board = permissionCheck(authorizationHeader, id, method, false);

        CollabOutputDTO collab = collabService.mapOutputDTO(collabService.deleteCollab(id, collab_oid));
        return ResponseEntity.ok().body(new HashMap<>());
    }
}

