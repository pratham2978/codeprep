package com.example.demo.controller;

import com.example.demo.model.ResumeProfile;
import com.example.demo.repository.ResumeProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeProfileRepository repo;

    // Load resume for a user (create empty if doesn't exist)
    @GetMapping("/{username}")
    public ResponseEntity<ResumeProfile> getResume(@PathVariable String username) {
        Optional<ResumeProfile> existing = repo.findByUsername(username);
        if (existing.isPresent()) {
            return ResponseEntity.ok(existing.get());
        }
        // Return an empty template
        ResumeProfile empty = new ResumeProfile();
        empty.setUsername(username);
        return ResponseEntity.ok(empty);
    }

    // Save / update a resume
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveResume(@RequestBody ResumeProfile incoming) {
        Optional<ResumeProfile> existing = repo.findByUsername(incoming.getUsername());
        ResumeProfile profile = existing.orElse(new ResumeProfile());

        profile.setUsername(incoming.getUsername());
        profile.setFullName(incoming.getFullName());
        profile.setEmail(incoming.getEmail());
        profile.setPhone(incoming.getPhone());
        profile.setLinkedin(incoming.getLinkedin());
        profile.setGithub(incoming.getGithub());
        profile.setSummary(incoming.getSummary());
        profile.setSkills(incoming.getSkills());
        profile.setExperience(incoming.getExperience());
        profile.setEducation(incoming.getEducation());
        profile.setProjects(incoming.getProjects());
        profile.setLastUpdated(LocalDateTime.now());

        repo.save(profile);

        // Compute ATS score based on content completeness
        int atsScore = computeAtsScore(profile);

        return ResponseEntity.ok(Map.of(
                "message", "Resume saved successfully!",
                "atsScore", atsScore,
                "lastUpdated", profile.getLastUpdated().toString()));
    }

    // ATS Score logic: reward completeness
    private int computeAtsScore(ResumeProfile p) {
        int score = 0;
        if (p.getFullName() != null && !p.getFullName().isBlank())
            score += 5;
        if (p.getEmail() != null && !p.getEmail().isBlank())
            score += 5;
        if (p.getPhone() != null && !p.getPhone().isBlank())
            score += 5;
        if (p.getLinkedin() != null && !p.getLinkedin().isBlank())
            score += 10;
        if (p.getGithub() != null && !p.getGithub().isBlank())
            score += 10;
        if (p.getSummary() != null && p.getSummary().length() > 50)
            score += 15;
        if (p.getSkills() != null && p.getSkills().length() > 10)
            score += 20;
        if (p.getExperience() != null && p.getExperience().length() > 10)
            score += 15;
        if (p.getEducation() != null && p.getEducation().length() > 10)
            score += 10;
        if (p.getProjects() != null && p.getProjects().length() > 10)
            score += 5;
        return Math.min(score, 100);
    }
}
