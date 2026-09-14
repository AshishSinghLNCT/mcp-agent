package com.ashishsingh.mcptools.tool;

import com.ashishsingh.mcptools.model.GitHubIssue;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * MCP tool that reads open issues straight from the public GitHub REST API.
 * <p>
 * No SDK dependency is needed for this: GitHub's REST API is plain JSON over HTTPS, so a
 * {@link RestClient} is enough. Works unauthenticated (60 req/hour, fine for a demo); set the
 * {@code GITHUB_TOKEN} env var to raise that to 5,000 req/hour and to read private repos you own.
 */
@Component
public class GitHubTool {

    private final RestClient restClient;

    public GitHubTool(@Value("${github.token:}") String githubToken) {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28");
        if (githubToken != null && !githubToken.isBlank()) {
            builder = builder.defaultHeader("Authorization", "Bearer " + githubToken);
        }
        this.restClient = builder.build();
    }

    @Tool(description = "Fetch open GitHub issues (not pull requests) for a given owner/repo, "
            + "e.g. owner='AshishSinghLNCT', repo='student-expense-tracker'.")
    public List<GitHubIssue> getOpenIssues(
            @ToolParam(description = "GitHub username or organization that owns the repository") String owner,
            @ToolParam(description = "Repository name") String repo
    ) {
        JsonNode response = restClient.get()
                .uri("/repos/{owner}/{repo}/issues?state=open&per_page=30", owner, repo)
                .retrieve()
                .body(JsonNode.class);

        List<GitHubIssue> issues = new ArrayList<>();
        if (response == null) {
            return issues;
        }
        for (JsonNode node : response) {
            // The GitHub "issues" endpoint also returns pull requests; skip those.
            if (node.has("pull_request")) {
                continue;
            }
            List<String> labels = new ArrayList<>();
            node.path("labels").forEach(label -> labels.add(label.path("name").asText()));

            issues.add(new GitHubIssue(
                    node.path("number").asLong(),
                    node.path("title").asText(),
                    owner + "/" + repo,
                    labels,
                    node.path("state").asText(),
                    node.path("created_at").asText(),
                    node.path("html_url").asText()
            ));
        }
        return issues;
    }
}
