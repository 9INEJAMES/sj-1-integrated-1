package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.BoardInputDTO;
import int221.integrated1backend.dtos.BoardOutputDTO;
import int221.integrated1backend.dtos.TaskOutputDTO;
import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.entities.in.TaskV2;
import int221.integrated1backend.services.BoardService;
import int221.integrated1backend.services.JwtTokenUtil;
import int221.integrated1backend.services.ListMapper;
import int221.integrated1backend.services.TaskV2Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v3/boards")
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://localhost:5173"})
public class BoardControllerV3 {
    @Autowired
    private BoardService boardService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private TaskV2Service taskService;
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
        List<BoardOutputDTO> boardOutputDTOList = boardService.mapOutputDTOList(boardList);
        return ResponseEntity.ok(boardOutputDTOList);
    }

    @PostMapping("")
    public ResponseEntity<Object> createBoard(@RequestHeader("Authorization") String authorizationHeader, @RequestBody BoardInputDTO boardInput) {
        String oid = getOidFromHeader(authorizationHeader);

        Board board = modelMapper.map(boardInput, Board.class);
        board.setOid(oid);

        Board newBoard = boardService.createNewBoard(board);
        BoardOutputDTO boardOutputDTO = boardService.mapOutputDTO(newBoard);

        return ResponseEntity.ok(boardOutputDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBoard(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) {
        String oid = getOidFromHeader(authorizationHeader);

        Board board = boardService.getBoard(id);
//        permissionCheck(board.getOid(), oid);
        BoardOutputDTO boardOutputDTO = boardService.mapOutputDTO(board);
        return ResponseEntity.ok(boardOutputDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBoard(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id, @RequestBody Board boardInput) {
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
    public ResponseEntity<Object> getTasks(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String id,
            @RequestParam(defaultValue = "") String[] filterStatuses,
            @RequestParam(defaultValue = "") String[] sortBy,
            @RequestParam(defaultValue = "ASC") String[] sortDirection) {
        String oid = getOidFromHeader(authorizationHeader);

        Board board = boardService.getBoard(id);
//        permissionCheck(board.getOid(), oid);

        if (filterStatuses.length > 0)
            return ResponseEntity.ok(taskService.getAllTaskOfBoard(id,filterStatuses, sortBy, sortDirection));
        else {
            List<TaskV2> taskList = taskService.getAllTaskOfBoard(id);
            List<TaskOutputDTO> taskDTO = listMapper.mapList(taskList, TaskOutputDTO.class, modelMapper);
            return ResponseEntity.ok(taskDTO);
        }
    }

}

