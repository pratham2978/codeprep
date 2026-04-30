package com.example.demo.controller;

import com.example.demo.model.UserActivity;
import com.example.demo.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*")
public class UserActivityController {

    @Autowired
    private UserActivityRepository repo;

    // Frontend pushes localStorage stats to DB to appear on leaderboard
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncActivity(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.getOrDefault("username", "Anonymous");
        int total = Integer.parseInt(payload.getOrDefault("totalSolved", 0).toString());
        int easy = Integer.parseInt(payload.getOrDefault("easySolved", 0).toString());
        int medium = Integer.parseInt(payload.getOrDefault("mediumSolved", 0).toString());
        int hard = Integer.parseInt(payload.getOrDefault("hardSolved", 0).toString());
        int submissions = Integer.parseInt(payload.getOrDefault("submissions", 0).toString());
        int streakDays = Integer.parseInt(payload.getOrDefault("streakDays", 0).toString());
        int bestStreak = Integer.parseInt(payload.getOrDefault("bestStreak", 0).toString());
        int totalPoints = Integer.parseInt(payload.getOrDefault("totalPoints", 0).toString());
        int quizzesTaken = Integer.parseInt(payload.getOrDefault("quizzesTaken", 0).toString());

        Optional<UserActivity> existing = repo.findByUsername(username);
        UserActivity activity = existing.orElse(new UserActivity(username));

        // Only update if new score is better — prevent cheating by going backwards
        if (total >= activity.getTotalSolved()) {
            activity.setTotalSolved(total);
            activity.setEasySolved(easy);
            activity.setMediumSolved(medium);
            activity.setHardSolved(hard);
        }
        // Always try to update streaks and points if they are greater
        if (streakDays >= activity.getStreakDays()) activity.setStreakDays(streakDays);
        if (bestStreak >= activity.getBestStreak()) activity.setBestStreak(bestStreak);
        if (totalPoints >= activity.getTotalPoints()) activity.setTotalPoints(totalPoints);
        if (submissions >= activity.getSubmissions()) activity.setSubmissions(submissions);
        if (quizzesTaken >= activity.getQuizzesTaken()) activity.setQuizzesTaken(quizzesTaken);
        activity.setLastActive(LocalDateTime.now());
        repo.save(activity);

        // Compute rank
        List<UserActivity> leaderboard = repo.findTop20ByOrderByTotalSolvedDesc();
        int rank = 1;
        for (UserActivity a : leaderboard) {
            if (a.getUsername().equals(username))
                break;
            rank++;
        }

        return ResponseEntity.ok(Map.of(
                "message", "Progress synced!",
                "rank", rank,
                "totalSolved", activity.getTotalSolved()));
    }

    // Top 20 global leaderboard
    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserActivity>> getLeaderboard() {
        return ResponseEntity.ok(repo.findTop20ByOrderByTotalSolvedDesc());
    }

    // Individual user profile
    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        return repo.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
