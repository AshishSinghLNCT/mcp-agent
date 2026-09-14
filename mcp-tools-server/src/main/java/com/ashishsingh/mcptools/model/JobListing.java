package com.ashishsingh.mcptools.model;

public record JobListing(
        String title,
        String company,
        String location,
        String applyUrl,
        String source
) {
}
