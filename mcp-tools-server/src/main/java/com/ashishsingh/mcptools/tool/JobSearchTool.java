package com.ashishsingh.mcptools.tool;

import com.ashishsingh.mcptools.model.JobListing;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MCP tool for job search.
 * <p>
 * <b>Honesty note:</b> this is a stub that filters an in-memory sample list, not a live
 * integration — real job-board APIs (Adzuna, JSearch via RapidAPI, LinkedIn, Naukri) need an
 * API key that isn't included here. The method signature and tool contract are real; swap the
 * body of {@link #searchJobs} for an HTTP call to whichever provider you register a key with and
 * the rest of the agent (prompting, MCP wiring) needs no changes. Don't describe this tool as a
 * "live job board integration" on a resume unless you've actually wired a real provider in.
 */
@Component
public class JobSearchTool {

    private static final List<JobListing> SAMPLE_LISTINGS = List.of(
            new JobListing("Software Engineer I (New Grad)", "Atlassian", "Bengaluru, IN",
                    "https://www.atlassian.com/company/careers", "sample-data"),
            new JobListing("Java Backend Developer - Fresher", "Razorpay", "Bengaluru, IN",
                    "https://razorpay.com/jobs/", "sample-data"),
            new JobListing("Associate Software Engineer", "Zoho Corporation", "Chennai, IN",
                    "https://www.zoho.com/careers/", "sample-data"),
            new JobListing("Graduate Engineer Trainee - Backend", "Walmart Global Tech", "Bengaluru, IN",
                    "https://careers.walmart.com/global-tech", "sample-data"),
            new JobListing("Software Developer Intern", "Ola", "Bengaluru, IN",
                    "https://www.olacabs.com/careers", "sample-data")
    );

    @Tool(description = "Search for entry-level / fresher software engineering job postings by keyword and location. "
            + "NOTE: returns sample data — wire a real provider (Adzuna, JSearch, etc.) before relying on this for a live job hunt.")
    public List<JobListing> searchJobs(
            @ToolParam(description = "Role keyword, e.g. 'backend', 'java', 'data science'") String keyword,
            @ToolParam(description = "City or 'remote'", required = false) String location
    ) {
        String needle = keyword == null ? "" : keyword.toLowerCase();
        return SAMPLE_LISTINGS.stream()
                .filter(job -> needle.isBlank()
                        || job.title().toLowerCase().contains(needle)
                        || job.company().toLowerCase().contains(needle))
                .filter(job -> location == null || location.isBlank()
                        || job.location().toLowerCase().contains(location.toLowerCase()))
                .toList();
    }
}
