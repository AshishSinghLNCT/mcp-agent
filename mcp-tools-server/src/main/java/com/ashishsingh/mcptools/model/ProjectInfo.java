package com.ashishsingh.mcptools.model;

public record ProjectInfo(
        String name,
        String priority,
        String techStack,
        String deadline,
        String description
) {
}
