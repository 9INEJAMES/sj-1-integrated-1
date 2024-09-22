package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.JwtRequestUser;
import int221.integrated1backend.dtos.Token;
import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.services.JwtTokenUtil;
import int221.integrated1backend.services.JwtUserDetailsService;
import int221.integrated1backend.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("")
@CrossOrigin(origins = {"http://ip23sj1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th", "http://localhost:5173"})
public class AuthenController {
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid JwtRequestUser jwtRequestUser) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
//        if (!authentication.isAuthenticated()) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password incorrect");//////////////exception
//        }
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(jwtRequestUser.getUserName());
        String access_token = jwtTokenUtil.generateToken(userDetails.getUsername());
        String refresh_token = jwtTokenUtil.generateRefreshToken(userDetails.getUsername());
        Token tokenObj = new Token();
        tokenObj.setAccess_token(access_token);
        tokenObj.setRefresh_token(refresh_token);
        return ResponseEntity.ok(tokenObj);
    }

    @PostMapping("/token")
    public ResponseEntity<Object> validateToken(@RequestHeader("Authorization") @Valid String requestTokenHeader) {
        Claims claims = null;
        String refresh_token = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            refresh_token = requestTokenHeader.substring(7);
            try {
                claims = jwtTokenUtil.getAllClaimsFromToken(refresh_token);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
                    "JWT Token does not begin with Bearer String");
        }
        User user = userService.findByOid(jwtTokenUtil.getClaimValueFromToken(refresh_token,"oid"));
        String access_token = jwtTokenUtil.generateToken(user.getUsername());
        Token tokenObj = new Token();
        tokenObj.setAccess_token(access_token);
        tokenObj.setRefresh_token(refresh_token);

        return ResponseEntity.ok(tokenObj);
    }
}


