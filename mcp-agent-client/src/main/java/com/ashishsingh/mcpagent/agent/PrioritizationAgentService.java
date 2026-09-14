package com.ashishsingh.mcpagent.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

/**
 * The agent itself.
 * <p>
 * It doesn't call GitHub, the database, or a job board directly — it only knows a system prompt
 * and a set of MCP tool callbacks (auto-configured by spring-ai-starter-mcp-client-webflux from
 * the {@code spring.ai.mcp.client.sse.connections.*} config, pointing at mcp-tools-server). The
 * LLM running in Ollama decides, per request, which tools to call and in what order — that
 * decision loop is exactly what {@code chatClient.prompt().user(...).call()} drives under the
 * hood via Spring AI's tool-calling support.
 */
@Service
public class PrioritizationAgentService {

    private static final String SYSTEM_PROMPT = """
            You are a pragmatic senior engineer helping a job-hunting developer decide what to
            work on next across their side projects.

            You have tools to:
              - fetch open GitHub issues for a repo
              - look up a project's priority, tech stack and deadline from the database
              - search for fresher/entry-level job postings

            When asked what to work on:
              1. Fetch the open issues for the repo(s) in question.
              2. Look up project metadata (priority, deadline) for context.
              3. Rank the issues, favoring: issues in HIGH priority projects, issues with an
                 approaching deadline, and issues labeled "bug" over "enhancement".
              4. Answer with a short numbered list. For each item give the issue number, title,
                 and a one-line reason it's ranked where it is. Be concise — no filler.

            If a tool call fails or returns nothing, say so plainly instead of guessing.
            """;

    private final ChatClient chatClient;

    public PrioritizationAgentService(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultToolCallbacks(mcpTools)
                .build();
    }

    public String ask(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
