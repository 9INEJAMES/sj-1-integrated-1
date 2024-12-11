package int221.integrated1backend.controllers;

import int221.integrated1backend.dtos.AuthResponse;
import int221.integrated1backend.dtos.AuthRequest;
import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.exceptions.UnauthenticatedException;
import int221.integrated1backend.models.TokenType;
import int221.integrated1backend.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("")
public class AuthenController {
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    AzureService azureService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
        );
        User user = jwtUserDetailsService.loadUserByUsername(request.getUserName());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        String access_token = jwtService.generateAccessToken(user);
        String refresh_token = jwtService.generateRefreshToken(user);

        AuthResponse authResponse = new AuthResponse(access_token, refresh_token);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/token")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestHeader("Authorization") String requestTokenHeader,
            @RequestHeader("Auth-Type") String authType
    ) throws UnauthenticatedException {
        String refreshToken = requestTokenHeader.substring(7);
        if (authType != null && authType.equals("AZURE")) {
            AuthResponse response = azureService.refreshAccessToken(refreshToken);
            if (response == null) {
                return ResponseEntity.status(401).build();
            }
            return ResponseEntity.ok(response);
        }

        String oid = jwtService.getClaimValueFromToken(refreshToken, jwtService.getRefreshKey(), "oid");
        if (oid != null && jwtService.validateRefreshToken(refreshToken, oid)) {
            User user = jwtUserDetailsService.loadUserByOid(oid);
            String newToken = jwtService.generateAccessToken(user);

            AuthResponse authResponse = new AuthResponse(newToken, null);
            return ResponseEntity.ok(authResponse);
        }

        return ResponseEntity.status(401).build();
    }

}


