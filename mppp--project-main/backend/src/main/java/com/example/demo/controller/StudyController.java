package com.example.demo.controller;

import com.example.demo.model.StudyProgress;
import com.example.demo.model.StudyTopic;
import com.example.demo.repository.StudyProgressRepository;
import com.example.demo.repository.StudyTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/study")
@CrossOrigin(origins = "*")
public class StudyController {

    @Autowired
    private StudyTopicRepository topicRepo;

    @Autowired
    private StudyProgressRepository progressRepo;

    // Get all topics for a module (core-cs, system-design, oops)
    @GetMapping("/topics")
    public ResponseEntity<List<StudyTopic>> getTopics(@RequestParam String module) {
        return ResponseEntity.ok(topicRepo.findByModule(module));
    }

    // Get single topic content
    @GetMapping("/topics/{id}")
    public ResponseEntity<?> getTopic(@PathVariable Long id) {
        return topicRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Mark a topic as complete for a user
    @PostMapping("/progress/complete")
    public ResponseEntity<Map<String, Object>> markComplete(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.getOrDefault("username", "Anonymous");
        Long topicId = Long.parseLong(payload.get("topicId").toString());

        Optional<StudyProgress> existing = progressRepo.findByUsernameAndTopicId(username, topicId);
        if (existing.isEmpty()) {
            progressRepo.save(new StudyProgress(username, topicId));
        }

        long completedCount = progressRepo.countByUsernameAndCompleted(username, true);
        long totalCount = topicRepo.count();

        return ResponseEntity.ok(Map.of(
                "message", "Topic marked as complete!",
                "completedTopics", completedCount,
                "totalTopics", totalCount,
                "progressPercent", totalCount > 0 ? Math.round((double) completedCount / totalCount * 100) : 0));
    }

    // Get progress for a specific user
    @GetMapping("/progress")
    public ResponseEntity<Map<String, Object>> getProgress(@RequestParam String username) {
        List<StudyProgress> completed = progressRepo.findByUsername(username);
        long total = topicRepo.count();
        long done = completed.size();

        return ResponseEntity.ok(Map.of(
                "completedTopicIds", completed.stream().map(StudyProgress::getTopicId).toList(),
                "completedCount", done,
                "totalCount", total,
                "progressPercent", total > 0 ? Math.round((double) done / total * 100) : 0));
    }
}
