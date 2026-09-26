package com.example.codereviewagent.model;

import java.util.List;

/**
 * This is what the BACKEND sends back to the frontend after the AI review is done.
 * The frontend JavaScript will take this and display it nicely on the page.
 */
public class ReviewResponse {

    private int overallScore;        // a score out of 100
    private String summary;          // 2-3 line overall summary of the code
    private List<Issue> issues;      // list of problems found
    private List<String> strengths;  // list of things the code does well

    public ReviewResponse() {
    }

    public ReviewResponse(int overallScore, String summary, List<Issue> issues, List<String> strengths) {
        this.overallScore = overallScore;
        this.summary = summary;
        this.issues = issues;
        this.strengths = strengths;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }
}
