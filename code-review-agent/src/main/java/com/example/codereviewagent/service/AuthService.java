package com.example.codereviewagent.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Very simple authentication for a student project - just checks username/password
 * against a hardcoded in-memory map. No database needed.
 *
 * In a real production app you would NEVER store plain-text passwords like this -
 * you'd store hashed passwords (e.g. with BCrypt) in a database. For a college
 * project demo, this keeps things simple and easy to explain.
 */
@Service
public class AuthService {

    // Demo users: username -> password
    private static final Map<String, String> USERS = Map.of(
            "student", "password123",
            "admin", "admin123"
    );

    public boolean isValidUser(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        String storedPassword = USERS.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }
}
