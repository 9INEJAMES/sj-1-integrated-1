package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.BoardInputDTO;
import int221.integrated1backend.dtos.BoardOutputDTO;
import int221.integrated1backend.dtos.Owner;
import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.services.BoardService;
import int221.integrated1backend.services.JwtTokenUtil;
import int221.integrated1backend.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/v3/boards")
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://localhost:5173"})
public class BoardControllerV3 {
    @Autowired
    private BoardService service;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;


    @GetMapping("")
    public ResponseEntity<Object> getAllBoard(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String oid = jwtTokenUtil.getClaimValueFromToken(token,"oid");
        List<Board> boardList = service.getBoardByOId(oid);
        System.out.println(boardList);
        return ResponseEntity.ok(boardList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBoard(@PathVariable String id) {
        Board board = service.getBoard(id);
        BoardOutputDTO boardOutputDTO = modelMapper.map(board, BoardOutputDTO.class);
        Owner owner = new Owner();
        owner.setOid(board.getOid());
        String uName = userService.findByOid(board.getOid()).getName();
        owner.setName(uName);
        boardOutputDTO.setOwner(owner);
        return ResponseEntity.ok(boardOutputDTO);
    }

    @PutMapping("")
    public ResponseEntity<Object> updateLimitTask(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Board boardInput) {
        Board board = service.updateLimitTask(boardInput);
        return ResponseEntity.ok(board);
    }

    @PostMapping("")
    public ResponseEntity<Object> createBoard(@RequestHeader("Authorization") String authorizationHeader, @RequestBody BoardInputDTO boardInput) {
        String token = authorizationHeader.substring(7);
        String oid = jwtTokenUtil.getClaimValueFromToken(token,"oid");
        String uName = jwtTokenUtil.getClaimValueFromToken(token,"name");

        Board board = modelMapper.map(boardInput, Board.class);
        board.setOid(oid);
        Board newBoard = service.createNewBoard(board);

        BoardOutputDTO boardOutputDTO = modelMapper.map(board, BoardOutputDTO.class);
        Owner owner = new Owner();
        owner.setOid(newBoard.getOid());
        owner.setName(uName);
        boardOutputDTO.setOwner(owner);

        return ResponseEntity.ok(boardOutputDTO);
    }
}

