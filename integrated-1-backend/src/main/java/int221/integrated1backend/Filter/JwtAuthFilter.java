package int221.integrated1backend.Filter;

import int221.integrated1backend.entities.ex.User;
import int221.integrated1backend.models.AuthType;
import int221.integrated1backend.models.TokenType;
import int221.integrated1backend.services.AzureService;
import int221.integrated1backend.services.JwtService;
import int221.integrated1backend.services.JwtUserDetailsService;
import int221.integrated1backend.services.UserService;
import int221.integrated1backend.utils.Constant;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AzureService azureService;
    private final JwtUserDetailsService userDetailsService;
    private final UserService userService;

    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(Constant.PUBLIC_ENDPOINTS);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PUBLIC_ENDPOINTS.contains(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, java.io.IOException {
        String authHeader = request.getHeader("Authorization");
        String authType = request.getHeader("Auth-Type");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        if ("AZURE".equals(authType)) {
            handleAzureAuth(request, response, filterChain, token);
        } else {
            handleLocalAuth(request, response, filterChain, token);
        }
    }

    private void handleAzureAuth(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String token) throws IOException, ServletException, java.io.IOException {
        User user = azureService.fetchUserDetails(token);

        if (user == null) {
            return;
        }

        user.setAuthType(AuthType.AZURE);
        user.setAccessToken(token);
        setAuthentication(request, user);

//        if(userService.findByEmail(user.getEmail())==null){
            azureService.cacheUserDetails(user);
//        }


        filterChain.doFilter(request, response);
    }

    private void handleLocalAuth(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String token) throws IOException, ServletException, java.io.IOException {
        String oid;

        oid = jwtService.getClaimValueFromToken(token, TokenType.ACCESS, "oid");


        if (oid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userDetailsService.loadUserByOid(oid);
            user.setAuthType(AuthType.LOCAL);
            user.setAccessToken(token);

            if (jwtService.validateAccessToken(token, oid)) {
                setAuthentication(request, user);
            } else {
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request, User user) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException, java.io.IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message);
    }
}