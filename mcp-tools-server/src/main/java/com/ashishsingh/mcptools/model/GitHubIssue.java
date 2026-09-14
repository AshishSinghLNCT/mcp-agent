package com.ashishsingh.mcptools.model;

import java.util.List;

/**
 * Minimal projection of a GitHub issue, trimmed down to what the agent needs to reason about
 * priority (title, labels, age) without shipping the whole GitHub API payload to the LLM.
 */
public record GitHubIssue(
        long number,
        String title,
        String repository,
        List<String> labels,
        String state,
        String createdAt,
        String htmlUrl
) {
}
