package com.example.codereviewagent.service;

import com.example.codereviewagent.model.Issue;
import com.example.codereviewagent.model.ReviewResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the ONLY place in the project that talks to the AI (Google Gemini).
 * If you ever want to switch to a different AI provider, this is the only file
 * you'd need to change.
 */
@Service
public class AiClientService {

    // Reads the API key from application.properties (which reads it from an environment variable)
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends the code to Gemini and asks it to review it.
     * We instruct the AI to reply ONLY in JSON so we can easily convert
     * the reply into our ReviewResponse object.
     */
    public ReviewResponse reviewCode(String code, String language) {

        String prompt = buildPrompt(code, language);

        // 1. Build the request body Gemini's API expects
        String requestBody = buildRequestBody(prompt);

        // 2. Gemini takes the API key as a header (x-goog-api-key), not in the URL
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-goog-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // 3. Call the Gemini API - the model name is part of the URL itself
        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent";
        String rawResponse = restTemplate.postForObject(url, requestEntity, String.class);

        // 4. Extract the AI's text reply and convert it into our ReviewResponse object
        return parseAiResponse(rawResponse);
    }

    private String buildPrompt(String code, String language) {
        return """
                You are an experienced senior software engineer doing a code review.
                Review the following %s code and respond with ONLY a valid JSON object
                (no markdown, no code fences, no extra text) in exactly this shape:

                {
                  "overallScore": <integer 0-100>,
                  "summary": "<2-3 sentence overall summary>",
                  "issues": [
                    {
                      "severity": "CRITICAL" | "MAJOR" | "MINOR" | "NIT",
                      "category": "<e.g. Naming, Security, Performance, Bug, Style>",
                      "line": <int, 0 if not applicable>,
                      "description": "<what is wrong>",
                      "suggestion": "<how to fix it>"
                    }
                  ],
                  "strengths": ["<thing the code does well>", "..."]
                }

                Here is the code:
                ```%s
                %s
                ```
                """.formatted(language, language, code);
    }

    private String buildRequestBody(String prompt) {
        try {
            // Gemini's request shape is different from Anthropic's:
            // { "contents": [ { "parts": [ { "text": "..." } ] } ] }
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("contents", List.of(content));

            return objectMapper.writeValueAsString(requestMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build request to AI: " + e.getMessage(), e);
        }
    }

    private ReviewResponse parseAiResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            // Gemini's response looks like:
            // { "candidates": [ { "content": { "parts": [ { "text": "..." } ] } } ] }
            String aiText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            // Gemini sometimes wraps its JSON in ```json ... ``` even when told not to.
            // Strip any markdown code fences before parsing, just in case.
            String cleanedText = aiText
                    .replaceAll("(?s)```json", "")
                    .replaceAll("(?s)```", "")
                    .trim();

            // The AI was told to reply with pure JSON, so we parse that text as JSON too
            JsonNode reviewJson = objectMapper.readTree(cleanedText);

            int score = reviewJson.path("overallScore").asInt(0);
            String summary = reviewJson.path("summary").asText("No summary provided.");

            List<Issue> issues = new ArrayList<>();
            for (JsonNode issueNode : reviewJson.path("issues")) {
                issues.add(new Issue(
                        issueNode.path("severity").asText("MINOR"),
                        issueNode.path("category").asText("General"),
                        issueNode.path("line").asInt(0),
                        issueNode.path("description").asText(""),
                        issueNode.path("suggestion").asText("")
                ));
            }

            List<String> strengths = new ArrayList<>();
            for (JsonNode strengthNode : reviewJson.path("strengths")) {
                strengths.add(strengthNode.asText());
            }

            return new ReviewResponse(score, summary, issues, strengths);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage(), e);
        }
    }
}
