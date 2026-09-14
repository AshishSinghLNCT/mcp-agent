package com.ashishsingh.mcptools.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Matches the GitHub repository name so the agent can join issues to project metadata. */
    private String repoName;

    private String priority;      // HIGH, MEDIUM, LOW
    private String techStack;
    private String deadline;      // free text, e.g. "2026-10-15" or "No fixed deadline"
    private String description;

    protected Project() {
        // required by JPA
    }

    public Project(String repoName, String priority, String techStack, String deadline, String description) {
        this.repoName = repoName;
        this.priority = priority;
        this.techStack = techStack;
        this.deadline = deadline;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getPriority() {
        return priority;
    }

    public String getTechStack() {
        return techStack;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getDescription() {
        return description;
    }
}
