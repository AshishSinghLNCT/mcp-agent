package com.ashishsingh.mcptools.tool;

import com.ashishsingh.mcptools.entity.Project;
import com.ashishsingh.mcptools.model.ProjectInfo;
import com.ashishsingh.mcptools.repository.ProjectRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MCP tool backed by an in-memory H2 database (seeded from data.sql). Gives the agent metadata
 * that GitHub itself doesn't have an opinion on: which project this repo belongs to, how the
 * user has prioritized it, and any deadline. This is what lets the agent tell the difference
 * between "urgent bug in a HIGH priority project" and "nice-to-have in a shelved side project".
 */
@Component
public class DatabaseTool {

    private final ProjectRepository projectRepository;

    public DatabaseTool(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Tool(description = "Look up priority, tech stack and deadline for one of the user's projects by repo name.")
    public ProjectInfo getProjectInfo(
            @ToolParam(description = "Repository name, e.g. 'student-expense-tracker'") String repoName
    ) {
        return projectRepository.findByRepoNameIgnoreCase(repoName)
                .map(this::toProjectInfo)
                .orElseGet(() -> new ProjectInfo(repoName, "UNKNOWN",
                        "not tracked in the database", "n/a",
                        "No metadata found for this repo; treat it as unranked."));
    }

    @Tool(description = "List every project the user is tracking, with priority and deadline for each.")
    public List<ProjectInfo> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::toProjectInfo)
                .toList();
    }

    private ProjectInfo toProjectInfo(Project project) {
        return new ProjectInfo(
                project.getRepoName(),
                project.getPriority(),
                project.getTechStack(),
                project.getDeadline(),
                project.getDescription()
        );
    }
}
