package com.ashishsingh.mcptools.repository;

import com.ashishsingh.mcptools.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByRepoNameIgnoreCase(String repoName);
}
