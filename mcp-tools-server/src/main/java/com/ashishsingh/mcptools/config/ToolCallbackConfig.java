package com.ashishsingh.mcptools.config;

import com.ashishsingh.mcptools.tool.DatabaseTool;
import com.ashishsingh.mcptools.tool.GitHubTool;
import com.ashishsingh.mcptools.tool.JobSearchTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the three {@code @Tool}-annotated beans as a single {@link ToolCallbackProvider}.
 * The spring-ai-starter-mcp-server-webmvc autoconfiguration picks this bean up and exposes every
 * tool on it over MCP (SSE / Streamable HTTP) — no manual endpoint wiring needed.
 */
@Configuration
public class ToolCallbackConfig {

    @Bean
    public ToolCallbackProvider agentTools(GitHubTool gitHubTool,
                                            DatabaseTool databaseTool,
                                            JobSearchTool jobSearchTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(gitHubTool, databaseTool, jobSearchTool)
                .build();
    }
}
