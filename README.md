# MCP Job-Prioritization Agent

A small AI agent built with **Java + Spring Boot + Spring AI + Model Context Protocol (MCP) + Ollama**.
It answers questions like:

> "Find my open GitHub issues and tell me which ones I should work on first."

by deciding, on its own, which tools to call and in what order — not a hardcoded pipeline.

## Architecture

```
                         AI Agent (mcp-agent-client, :8080)
                    ChatClient + Ollama (local LLM) + MCP client
                                    │
                                    │  MCP over SSE
                                    ▼
                    MCP Tools Server (mcp-tools-server, :8081)
        ┌───────────────────┬───────────────────┬───────────────────┐
        ▼                   ▼                   ▼
   GitHub Tool         Database Tool       Job Search Tool
  (GitHub REST API)   (H2 + Spring Data)   (sample data, stub)
```

**Request flow for the example question:**

```
User → Agent:  "Find my open GitHub issues and tell me which ones I should work on first."
Agent → LLM:   here's the system prompt + your question + the tool list
LLM   → Agent: "call getOpenIssues(owner, repo)"
Agent → MCP Tools Server → GitHub REST API → open issues
LLM   → Agent: "call getProjectInfo(repo)"
Agent → MCP Tools Server → H2 DB → priority / deadline
LLM   → Agent: ranks the issues, writes the final answer
Agent → User:  numbered, justified priority list
```

## Modules

| Module | What it is | Port |
|---|---|---|
| `mcp-tools-server` | Spring Boot app exposing 3 MCP tools: `GitHubTool`, `DatabaseTool`, `JobSearchTool` | 8081 |
| `mcp-agent-client` | Spring Boot app: Ollama-backed `ChatClient` + MCP client, exposes `POST /api/agent/ask` | 8080 |

### Tools

- **GitHubTool** — real integration. Calls the public GitHub REST API (`api.github.com`) for a
  repo's open issues. Works unauthenticated (60 req/hr); set `GITHUB_TOKEN` for 5,000 req/hr and
  private-repo access.
- **DatabaseTool** — real integration. Spring Data JPA over an in-memory H2 database, seeded
  (`data.sql`) with priority/deadline metadata for a few sample projects.
- **JobSearchTool** — **stub**. Filters a hardcoded in-memory list; it is *not* wired to a live
  job board. Swapping in a real provider (Adzuna, JSearch/RapidAPI, etc.) only requires rewriting
  the body of `searchJobs()` — the `@Tool` contract and the rest of the agent don't change. Don't
  claim a "live job board integration" on a resume unless you've actually done that swap.

## Prerequisites

- JDK 17+
- Maven 3.9+
- [Ollama](https://ollama.com/download) installed and running locally, with a model pulled:
  ```bash
  ollama pull llama3.1
  ```

## Run it

Build (from the repo root — this project wasn't compiled in the sandbox it was drafted in, since
that environment has no access to Maven Central; run this locally first and fix any dependency
version drift before you demo it):

```bash
mvn clean install
```

Start the tools server:

```bash
cd mcp-tools-server
mvn spring-boot:run
```

In a second terminal, start the agent:

```bash
cd mcp-agent-client
mvn spring-boot:run
```

Ask it something:

```bash
curl -X POST http://localhost:8080/api/agent/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"Find my open issues in AshishSinghLNCT/student-expense-tracker and tell me which ones I should work on first."}'
```

## Known limitations (be upfront about these in an interview)

- `JobSearchTool` is a stub over sample data, not a live job-board integration.
- No auth on the `/api/agent/ask` endpoint — fine for a local demo, not for deploying publicly as-is.
- Only tested against `llama3.1` via Ollama; smaller local models may follow the tool-calling
  instructions less reliably.
- MCP client/server config uses SSE — Spring AI's MCP APIs are still evolving fast, so pin the
  `spring-ai.version` in the root `pom.xml` and check the
  [MCP reference docs](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html) if
  something doesn't compile after a `mvn clean install`.

## Suggested resume line

> Built a multi-agent Java backend (Spring Boot, Spring AI, MCP, Ollama) that discovers tools
> over the Model Context Protocol and autonomously decides which to call — GitHub issue triage,
> project-priority lookups — to answer natural-language planning questions.

(Keep the job-search tool described as sample data unless you wire in a real provider.)
