package com.example.codereviewagent.model;

import jakarta.validation.constraints.NotBlank;

/**
 * This is what the FRONTEND sends to the backend when the user clicks "Review Code".
 * It gets automatically filled from the JSON body of the POST request.
 */
public class ReviewRequest {

    @NotBlank(message = "Please paste some code before submitting")
    private String code;

    private String language = "java"; // default if user doesn't pick one

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
