package com.example.codereviewagent.service;

import com.example.codereviewagent.model.ReviewResponse;
import org.springframework.stereotype.Service;

/**
 * This is the "business logic" layer.
 * The controller calls this, and this calls AiClientService.
 * Keeping this separate means: if you later want to add things like
 * "save every review to a database" or "don't allow more than 5 reviews/minute",
 * this is the file you'd edit - not the controller, not the AI client.
 */
@Service
public class CodeReviewService {

    private static final int MAX_CODE_LENGTH = 20_000; // simple safety limit

    private final AiClientService aiClientService;

    public CodeReviewService(AiClientService aiClientService) {
        this.aiClientService = aiClientService;
    }

    public ReviewResponse reviewCode(String code, String language) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code must not be empty.");
        }
        if (code.length() > MAX_CODE_LENGTH) {
            throw new IllegalArgumentException(
                    "Code is too long (" + code.length() + " characters). Limit is " + MAX_CODE_LENGTH + ".");
        }

        return aiClientService.reviewCode(code, language);
    }
}
