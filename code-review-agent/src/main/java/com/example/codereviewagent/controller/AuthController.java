package com.example.codereviewagent.controller;

import com.example.codereviewagent.model.LoginRequest;
import com.example.codereviewagent.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handles logging in, logging out, and checking whether someone is
 * currently logged in. Uses a normal HttpSession (Spring/Servlet built-in) -
 * no extra library needed to track who's logged in.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private static final String SESSION_KEY = "loggedInUser";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        if (!authService.isValidUser(request.getUsername(), request.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid username or password"));
        }

        // Create a session and remember who logged in
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(SESSION_KEY, request.getUsername());

        return ResponseEntity.ok(Map.of("username", request.getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @GetMapping("/session")
    public ResponseEntity<?> session(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        String username = (session != null) ? (String) session.getAttribute(SESSION_KEY) : null;

        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("loggedIn", false));
        }
        return ResponseEntity.ok(Map.of("loggedIn", true, "username", username));
    }
}
