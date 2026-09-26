package com.example.codereviewagent.controller;

import com.example.codereviewagent.model.ReviewRequest;
import com.example.codereviewagent.model.ReviewResponse;
import com.example.codereviewagent.service.CodeReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is the "front door" of the backend.
 * The frontend (index.html + script.js) sends a POST request to /api/review
 * with the code, and gets back a JSON review result.
 */
@RestController
public class CodeReviewController {

    private final CodeReviewService codeReviewService;

    public CodeReviewController(CodeReviewService codeReviewService) {
        this.codeReviewService = codeReviewService;
    }

    @PostMapping("/api/review")
    public ResponseEntity<ReviewResponse> review(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = codeReviewService.reviewCode(request.getCode(), request.getLanguage());
        return ResponseEntity.ok(response);
    }

    // If something goes wrong (empty code, code too long, AI call fails),
    // send back a readable error message instead of a scary stack trace.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadInput(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleServerError(RuntimeException ex) {
        return ResponseEntity.internalServerError().body("Something went wrong: " + ex.getMessage());
    }
}
