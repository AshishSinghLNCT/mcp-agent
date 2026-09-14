package com.ashishsingh.mcptools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Boots the MCP tools server.
 * <p>
 * This process exposes three tools over the Model Context Protocol so that any MCP-compatible
 * agent (including the mcp-agent-client module in this repo) can discover and call them:
 * <ul>
 *     <li>{@link com.ashishsingh.mcptools.tool.GitHubTool} - reads open issues from a GitHub repo</li>
 *     <li>{@link com.ashishsingh.mcptools.tool.DatabaseTool} - reads project metadata (priority, stack, deadline)</li>
 *     <li>{@link com.ashishsingh.mcptools.tool.JobSearchTool} - looks up matching job postings (stub/pluggable)</li>
 * </ul>
 */
@SpringBootApplication
public class McpToolsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpToolsServerApplication.class, args);
    }
}
