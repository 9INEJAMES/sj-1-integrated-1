package int221.integrated1backend.configs;

import int221.integrated1backend.Filter.JwtAuthFilter;
import int221.integrated1backend.exceptions.SimpleAuthenticationEntryPoint;
import int221.integrated1backend.services.JwtUserDetailsService;
import int221.integrated1backend.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Public access for GET requests to /v3/boards/**
                        .requestMatchers(HttpMethod.GET, "/v3/boards/**").permitAll()
                        // Require authentication for POST, PUT, PATCH, DELETE requests to /v3/boards/**
                        .requestMatchers(HttpMethod.POST, "/v3/boards/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v3/boards/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/v3/boards/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v3/boards/**").authenticated()
                        // Public access for other specified endpoints
                        .requestMatchers(Constant.PUBLIC_ENDPOINTS).permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session management
                .authenticationProvider(authenticationProvider()) // Set custom authentication provider
                .exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException) -> response.setStatus(401)))  // Set 401 status code for unauthenticated requests
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter before UsernamePasswordAuthenticationFilter

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(jwtUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
