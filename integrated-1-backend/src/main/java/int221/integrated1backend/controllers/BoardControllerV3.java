package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.*;
import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.entities.in.*;
import int221.integrated1backend.models.*;
import int221.integrated1backend.repositories.in.AttachmentRepository;
import int221.integrated1backend.repositories.in.TaskRepository;
import int221.integrated1backend.services.*;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;


@RestController
@RequestMapping("/v3/boards")
public class BoardControllerV3 {
    @Autowired
    private BoardService boardService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusService statusService;
    @Autowired
    private JwtService jwtTokenUtil;
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
    @Autowired
    private FileService fileService;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private AzureService azureService;


    private String getOidFromHeader(String header, String type) {
        if (header == null) return null;
        String token = header.substring(7);
        if ("AZURE".equals(type)) return azureService.fetchUserDetails(token).getOid();
        else return jwtTokenUtil.getClaimValueFromToken(token, TokenType.ACCESS, "oid");

    }

    private String getNameFromHeader(String header, String type) {
        if (header == null) return null;
        String token = header.substring(7);
        if ("AZURE".equals(type)) return azureService.fetchUserDetails(token).getName();
        else return jwtTokenUtil.getClaimValueFromToken(token, TokenType.ACCESS, "name");
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

    private Board permissionCheck(String authorizationHeader, String type, String bid, String method, Boolean isCollabCanDoOperation) {
        String userId = null;
        if (authorizationHeader != null) userId = getOidFromHeader(authorizationHeader, type);
        Board board = boardService.getBoard(bid);
        if ((!Objects.equals(method, "get") && board.getVisibility().equals(Visibility.PUBLIC) && userId != null) || board.getVisibility().equals(Visibility.PRIVATE)) {
            oidCheck(board, userId, method, board.getVisibility(), isCollabCanDoOperation);
        }
        return board;
    }

    @GetMapping("/{id}/access")
    public ResponseEntity<Object> getBoardAccess(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id) {
        Board board = permissionCheck(authorizationHeader, authType, id, "get", true);
        AccessRightDTO output = new AccessRightDTO();
        output.setAccessRight(String.valueOf(oidCheck(board, getOidFromHeader(authorizationHeader, authType), "get", board.getVisibility(), true)));
        return ResponseEntity.ok(output);
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllBoard(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType) {
        String userId = getOidFromHeader(authorizationHeader, authType);

        List<Board> boardList = boardService.getBoardByOId(userId);
        List<BoardOutputDTOwithLimit> boardOutputDTOList = boardService.mapOutputDTO(boardList, authType);
        List<CollabBoardOutputDTO> collabBoards = new ArrayList<>();

        /////////////
        List<Collab> collabs = collabService.getAllCollabBoardByOid(userId);
        for (Collab collab : collabs) {
            CollabBoardOutputDTO collabOutputDTO = modelMapper.map(boardService.mapOutputDTO(boardService.getBoard(collab.getBoardId()), authType), CollabBoardOutputDTO.class);
            collabOutputDTO.setAccessRight(collab.getAccessRight());
            collabOutputDTO.setStatus(collab.getStatus());
            collabBoards.add(collabOutputDTO);
        }

        TwoBoardListDTO output = new TwoBoardListDTO();
        output.setPerson_boards(boardOutputDTOList);
        output.setCollab_boards(collabBoards);

        return ResponseEntity.ok(output);
    }

    ///
    @PostMapping("")
    public ResponseEntity<Object> createBoard(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @Valid @RequestBody(required = false) BoardCreateInputDTO input) {
        String oid = getOidFromHeader(authorizationHeader, authType);
        if (input == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Board board = modelMapper.map(input, Board.class);
        board.setOid(oid);
        Board newBoard = boardService.createNewBoard(board);
        statusService.createNewDefaultStatus(board);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(newBoard, authType);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardOutputDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBoard(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id) {

        Board board = permissionCheck(authorizationHeader, authType, id, "get", true);

        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board, authType);
        return ResponseEntity.ok(boardOutputDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBoard(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @Valid @RequestBody(required = false) BoardInputDTO input) {
        Board existingBoard = permissionCheck(authorizationHeader, authType, id, "put", false);

        Board board = boardService.updateฺBoard(id, input);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board, authType);
        return ResponseEntity.ok(boardOutputDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateVisibilityOfBoard(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @Valid @RequestBody(required = false) BoardInputDTO input) {
        Board existingBoard = permissionCheck(authorizationHeader, authType, id, "patch", false);
        if (input == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Board board = boardService.updateฺBoard(id, input);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board, authType);
        return ResponseEntity.ok(boardOutputDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBoard(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id) {
        Board existingBoard = permissionCheck(authorizationHeader, authType, id, "delete", false);

        //delete all task and status in board!!
        taskService.removeAllTaskOfBoard(id);
        statusService.removeAllStatusOfBoard(id);
        collabService.removeAllCollabOfBoard(id);

        Board board = boardService.deleteBoard(id);
        BoardOutputDTOwithLimit boardOutputDTO = boardService.mapOutputDTO(board, authType);
        return ResponseEntity.ok(boardOutputDTO);
    }

    //Task operation

    @GetMapping("/{id}/tasks")
    public ResponseEntity<Object> getTasks(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @RequestParam(defaultValue = "") String[] filterStatuses) {
        Board board = permissionCheck(authorizationHeader, authType, id, "get", true);

        if (filterStatuses.length > 0) return ResponseEntity.ok(taskService.getAllTaskOfBoard(id, filterStatuses));
        else {
            List<Task> taskList = taskService.getAllTaskOfBoard(id);
            List<TaskOutputDTO> taskDTO = listMapper.mapList(taskList, TaskOutputDTO.class, modelMapper);
            return ResponseEntity.ok(taskDTO);
        }
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<Object> addNewTask(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @Valid @RequestBody(required = false) TaskInputDTO input) {
        Board board = permissionCheck(authorizationHeader, authType, id, "post", true);
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
    public ResponseEntity<Object> getTaskById(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable Integer taskId, @PathVariable String id) {
        Board board = permissionCheck(authorizationHeader, authType, id, "get", true);
        Task task = taskService.findByIdAndAndBoardId(taskId, id);
        TaskOutputAllFieldDTO outputDTO = modelMapper.map(task, TaskOutputAllFieldDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(outputDTO);
    }

    @PutMapping(path = "/{id}/tasks/{taskId}")
    public ResponseEntity<Object> updateTask(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable Integer taskId, @Valid @RequestPart("input") TaskInputDTO input, @RequestPart(name = "attachmentFiles", required = false) MultipartFile[] attachmentFiles) throws Exception {
        Board board = permissionCheck(authorizationHeader, authType, id, "put", true);
        Task oldTask = taskService.findByID(taskId);

        // Calculate the total size of existing attachments
        long taskFileSize = Optional.ofNullable(attachmentRepository.getTotalFileSizeByTaskId(taskId)).orElse(0L);
        final long MAX_ATTACHMENT_SIZE = 20 * 1024 * 1024 * 10;
        long totalSize = 0;

        // Identify attachments in the request
        List<String> requestAttachmentLocations = new ArrayList<>();
        List<RequestAttachment> requestAttachments = new ArrayList<>();

        //attachment of input
        for (Attachment attachmentFile : input.getAttachments()) {
            String fileName = Paths.get(attachmentFile.getLocation()).getFileName().toString();
            if (fileName != null) {
                RequestAttachment newAtt = new RequestAttachment();
                newAtt.setName(fileName);
                newAtt.setFileSize(attachmentFile.getFileSize());
                requestAttachments.add(newAtt);
                requestAttachmentLocations.add(fileName);
            }
        }
        if (oldTask.getAttachments() != null && !oldTask.getAttachments().isEmpty()) {
            for (Attachment attachment : oldTask.getAttachments()) {
                String fileName = Paths.get(attachment.getLocation()).getFileName().toString();
                if (!requestAttachmentLocations.contains(fileName)) {
                    try {
                        fileService.deleteFile(attachment.getAttachmentId());
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete unused attachment: " + e.getMessage());
                    }
                } else {
                    for (RequestAttachment requestAttachment : requestAttachments) {
                        if (Objects.equals(requestAttachment.getName(), fileName) && !Objects.equals(requestAttachment.getFileSize(), attachment.getFileSize())) {
                            fileService.deleteFile(attachment.getAttachmentId());
                        }
                    }
                }
            }
        }

        Task task = taskService.updateTask(taskId, input, id);

        //attachment of attachmentFiles
        if (attachmentFiles != null) {
            for (MultipartFile attachmentFile : attachmentFiles) {
                totalSize += attachmentFile.getSize();
            }
        }

        if (taskFileSize + totalSize > MAX_ATTACHMENT_SIZE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Total attachment size exceeds the 20MB limit.");
        }

        if (attachmentFiles != null) {
            for (MultipartFile attachmentFile : attachmentFiles) {
                try {
                    fileService.storeAttachment(attachmentFile, taskId);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store attachment: " + e.getMessage());
                }
            }
        }

        TaskOutputAllFieldDTO outputDTO = modelMapper.map(task, TaskOutputAllFieldDTO.class);
        boardService.updateฺInBoard(id);

        return ResponseEntity.ok(outputDTO);
    }


    @DeleteMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Object> deleteTask(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable Integer taskId) throws Exception {
        Board board = permissionCheck(authorizationHeader, authType, id, "delete", true);
        fileService.removeAllFileOfTask(taskService.findByID(taskId));
        TaskOutputDTO taskWithIdDTO = taskService.removeTask(taskId, id);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(taskWithIdDTO);
    }

    //all statuses (public)
    @GetMapping("/{id}/statuses")
    public ResponseEntity<Object> getAllStatus(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id) {
        Board board = permissionCheck(authorizationHeader, authType, id, "get", true);

        List<Status> statusList = statusService.getAllStatusByBoardId(id);
        List<StatusOutputDTO> outputDTOList = listMapper.mapList(statusList, StatusOutputDTO.class, modelMapper);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(outputDTOList);
    }

    @PostMapping("/{id}/statuses")
    public ResponseEntity<Object> addNewStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @Valid @RequestBody(required = false) StatusInputDTO input) {
        Board board = permissionCheck(authorizationHeader, authType, id, "post", true);

        Status status = statusService.createNewStatus(input, boardService.getBoard(id));
        StatusOutputDTO statusOutputDTO = modelMapper.map(status, StatusOutputDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(statusOutputDTO);
    }

    @GetMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> getStatusById(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable Integer statusId) {
        Board board = permissionCheck(authorizationHeader, authType, id, "get", true);

        Status status = statusService.findByID(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(modelMapper.map(status, StatusLimitOutputDTO.class));
    }

    ///fixed self status
    @PutMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable Integer statusId, @Valid @RequestBody(required = false) StatusInputDTO input) {
        Board board = permissionCheck(authorizationHeader, authType, id, "put", true);

        Status status = statusService.updateStatus(statusId, input);
        StatusOutputDTO statusOutputDTO = modelMapper.map(status, StatusOutputDTO.class);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(statusOutputDTO);
    }

    @DeleteMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> deleteStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable Integer statusId) {
        Board board = permissionCheck(authorizationHeader, authType, id, "delete", true);

        Status status = statusService.removeStatus(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    @DeleteMapping("/{id}/statuses/{statusId}/{newStatusId}")
    public ResponseEntity<Object> deleteStatus(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable Integer statusId, @PathVariable Integer newStatusId) {
        Board board = permissionCheck(authorizationHeader, authType, id, "delete", true);
        List<Task> TaskList = taskService.updateStatusOfTask(statusId, newStatusId);
        Status status = statusService.removeStatus(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    @PatchMapping("/{id}/statuses/{statusId}/maximum-task")
    public ResponseEntity<Object> updateMaximumTask(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable Integer statusId, @Valid @RequestBody(required = false) StatusInputDTO input) {
        Board board = permissionCheck(authorizationHeader, authType, id, "patch", true);
        if (input == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is missing or unreadable");
        }
        Status status = statusService.findByID(statusId);
        boardService.updateฺInBoard(id);
        return ResponseEntity.ok(modelMapper.map(status, StatusLimitOutputDTO.class));
    }

    @GetMapping("/{id}/collabs")
    public ResponseEntity<Object> getCollab(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id) {
        Board board = permissionCheck(authorizationHeader, authType, id, "get", true);
        List<CollabOutputDTO> collabs = collabService.mapOutputDTO(collabService.getAllCollabOfBoard(id));
        System.out.println(collabs);
        return ResponseEntity.ok(collabs);
    }

    @GetMapping("/{id}/collabs/{collab_oid}")
    public ResponseEntity<Object> getCollabById(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable String collab_oid) {
        Board board = permissionCheck(authorizationHeader, authType, id, "get", true);

        CollabOutputDTO collab = collabService.mapOutputDTO(collabService.getCollabOfBoard(id, collab_oid, true));
        return ResponseEntity.ok(collab);
    }

    @GetMapping("/{id}/collabs/invitation")
    public ResponseEntity<Object> getInvitation(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id) {
        String oid = getOidFromHeader(authorizationHeader, authType);

        CollabOutputDTOWithOwner collab = modelMapper.map(collabService.mapOutputDTO(collabService.getCollabOfBoard(id, oid, true)), CollabOutputDTOWithOwner.class);
        Board board = boardService.getBoard(id);
        collab.setBoardName(board.getName());
        collab.setOwnerName(userService.findByOid(board.getOid()).getName());
        collab.setCollaborators(collabService.getNumOfCollab(id));
        return ResponseEntity.ok(collab);
    }

    @PatchMapping("/{id}/collabs/invitation/{isAccept}")
    public ResponseEntity<Object> acceptedInvite(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable Boolean isAccept) {
        String oid = getOidFromHeader(authorizationHeader, authType);

        CollabOutputDTO collab = collabService.mapOutputDTO(collabService.updateCollabStatus(id, oid, isAccept));
        return ResponseEntity.ok(collab);
    }

    @PostMapping("/{id}/collabs")
    public ResponseEntity<Object> createCollab(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") AuthType authType, @PathVariable String id, @Valid @RequestBody(required = false) CollabCreateInputDTO input) throws MessagingException, UnsupportedEncodingException {
        Board board = permissionCheck(authorizationHeader, String.valueOf(authType), id, "post", false);
        User user = collabService.findUserByEmail(input.getEmail(),authType,authorizationHeader.substring(7));
        CollabOutputDTO newCollab = collabService.mapOutputDTO(collabService.createNewCollab(board, input, user));
        String userName = null;
        if (authorizationHeader != null) userName = getNameFromHeader(authorizationHeader, String.valueOf(authType));
        emailService.sendInviteEmail(input.getEmail(), userName, newCollab.getAccessRight(), board);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCollab);
    }

    @PatchMapping("/{id}/collabs/{collab_oid}")
    public ResponseEntity<Object> updateAccessRight(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable String collab_oid, @RequestBody(required = false) AccessRightDTO input) throws MessagingException, UnsupportedEncodingException {
        Board board = permissionCheck(authorizationHeader, authType, id, "patch", false);
        String userName = null;
        if (authorizationHeader != null) userName = getNameFromHeader(authorizationHeader, authType);
        CollabOutputDTO collab = collabService.mapOutputDTO(collabService.updateCollab(id, collab_oid, input));
//        if (collab.getStatus() == CollabStatus.PENDING) {
//            emailService.sendInviteEmail(collab.getEmail(), userName, collab.getAccessRight(), board);
//        }
        return ResponseEntity.ok(collab);
    }


    @DeleteMapping("/{id}/collabs/{collab_oid}")
    public ResponseEntity<Object> deleteCollab(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable String collab_oid) {
        String method = "delete";
        String oid = getOidFromHeader(authorizationHeader, authType);
        if (Objects.equals(oid, collab_oid)) method = "get";

        Board board = permissionCheck(authorizationHeader, authType, id, method, false);

        CollabOutputDTO collab = collabService.mapOutputDTO(collabService.deleteCollab(id, collab_oid));
        return ResponseEntity.ok().body(new HashMap<>());
    }

    @GetMapping("/{id}/tasks/{taskId}/attachments/{attachmentId}")
    public ResponseEntity<Resource> loadFile(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable Integer attachmentId) {
        Board board = permissionCheck(authorizationHeader, authType, id, "get", true);
        try {
            return fileService.loadFile(attachmentId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/tasks/{taskId}/attachments/displays/{filename:.+}")
    public ResponseEntity<Resource> loadFileDisplay(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable Integer taskId, @PathVariable String filename) throws UnsupportedEncodingException {

        Board board = permissionCheck(authorizationHeader, authType, id, "get", true);
        Resource fileResource = fileService.loadFileAsResource(taskId, filename);
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        // Encode filename in UTF-8 for compatibility
        String encodedFilename = URLEncoder.encode(Objects.requireNonNull(fileResource.getFilename()), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=UTF-8''" + encodedFilename).body(fileResource);
    }


    @DeleteMapping("/{id}/tasks/{taskId}/attachments/{attachmentId}")
    public ResponseEntity<String> deleteFile(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader("Auth-Type") String authType, @PathVariable String id, @PathVariable Integer attachmentId) {
        Board board = permissionCheck(authorizationHeader, authType, id, "get", true);
        try {
            String result = fileService.deleteFile(attachmentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

}

