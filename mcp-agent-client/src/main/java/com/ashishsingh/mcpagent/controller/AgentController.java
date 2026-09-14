package com.ashishsingh.mcpagent.controller;

import com.ashishsingh.mcpagent.agent.PrioritizationAgentService;
import com.ashishsingh.mcpagent.dto.AgentRequest;
import com.ashishsingh.mcpagent.dto.AgentResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentController {

    private final PrioritizationAgentService agentService;

    public AgentController(PrioritizationAgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * Example:
     * curl -X POST http://localhost:8080/api/agent/ask \
     *   -H "Content-Type: application/json" \
     *   -d '{"question":"Find my open GitHub issues in AshishSinghLNCT/student-expense-tracker and tell me which ones I should work on first."}'
     */
    @PostMapping("/api/agent/ask")
    public AgentResponse ask(@RequestBody AgentRequest request) {
        String answer = agentService.ask(request.question());
        return new AgentResponse(answer);
    }
}
