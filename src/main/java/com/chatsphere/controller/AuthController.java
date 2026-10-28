
package com.chatsphere.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatsphere.service.AuthService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody AuthRequest request,
            HttpSession session
    ) {
        if (request == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Request data is required."
                    )
            );
        }

        try {
            String username = authService.register(
                    request.getUsername(),
                    request.getPassword()
            );

            session.setAttribute("authenticatedUser", username);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Account created successfully.",
                            "username", username
                    )
            );

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", exception.getMessage()
                    )
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody AuthRequest request,
            HttpSession session
    ) {
        if (request == null
                || request.getUsername() == null
                || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Username and password are required."
                    )
            );
        }

        String username = authService.authenticate(
                request.getUsername(),
                request.getPassword()
        );

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of(
                            "success", false,
                            "message", "Invalid username or password."
                    )
            );
        }

        session.setAttribute("authenticatedUser", username);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Login successful.",
                        "username", username
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> currentUser(
            HttpSession session
    ) {
        Object username = session.getAttribute("authenticatedUser");

        if (!(username instanceof String)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of(
                            "success", false,
                            "message", "Please log in first."
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "username", username
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            HttpSession session
    ) {
        session.invalidate();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Logged out successfully."
                )
        );
    }

    public static class AuthRequest {

        private String username;
        private String password;

        public AuthRequest() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
