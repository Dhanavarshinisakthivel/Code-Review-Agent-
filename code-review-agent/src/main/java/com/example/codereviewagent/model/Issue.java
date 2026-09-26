package com.example.codereviewagent.model;

/**
 * Represents ONE problem that the AI found in the submitted code.
 * Example: "Line 12 - Variable name 'x' is not descriptive"
 */
public class Issue {

    private String severity;    // "CRITICAL", "MAJOR", "MINOR", or "NIT" (nitpick)
    private String category;    // e.g. "Naming", "Security", "Performance", "Bug"
    private int line;           // line number where the issue was found (0 if unknown)
    private String description; // what is wrong
    private String suggestion;  // how to fix it

    // Empty constructor is needed so Jackson (JSON library) can build this object automatically
    public Issue() {
    }

    public Issue(String severity, String category, int line, String description, String suggestion) {
        this.severity = severity;
        this.category = category;
        this.line = line;
        this.description = description;
        this.suggestion = suggestion;
    }

    // Getters and setters - Jackson needs these to convert JSON <-> Java object
    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
