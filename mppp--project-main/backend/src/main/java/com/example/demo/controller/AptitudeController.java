package com.example.demo.controller;

import com.example.demo.model.AptitudeQuestion;
import com.example.demo.model.AptitudeScore;
import com.example.demo.repository.AptitudeQuestionRepository;
import com.example.demo.repository.AptitudeScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aptitude")
@CrossOrigin(origins = "*") // Allows the frontend to freely call the APIs directly
public class AptitudeController {

    @Autowired
    private AptitudeQuestionRepository questionRepo;

    @Autowired
    private AptitudeScoreRepository scoreRepo;

    @GetMapping("/questions")
    public ResponseEntity<List<AptitudeQuestion>> getQuestions(@RequestParam String category,
            @RequestParam(defaultValue = "10") int limit) {
        // We use native RAND() query to shuffle and grab limited questions
        List<AptitudeQuestion> questions;
        if (category.equalsIgnoreCase("all")) {
            // Simplified fallback
            questions = questionRepo.findAll();
        } else {
            questions = questionRepo.findRandomByCategory(category.toLowerCase(), limit);
        }

        // SECURITY: Before sending to frontend, we must secure the payload so users
        // cannot cheat by inspecting the network tab!
        questions.forEach(q -> {
            q.setCorrectOption(null);
            q.setExplanation(null);
        });

        return ResponseEntity.ok(questions);
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitTest(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.getOrDefault("username", "Anonymous Candidate");
        String category = (String) payload.get("category");

        // Jackson parses nested JSON objects into generic Maps
        @SuppressWarnings("unchecked")
        Map<String, String> answers = (Map<String, String>) payload.get("answers");

        int score = 0;
        int total = answers != null ? answers.size() : 0;

        // We return a detailed review object containing the actual correct answers and
        // explanations natively to the client for review
        Map<String, Object> details = new HashMap<>();

        if (answers != null) {
            for (Map.Entry<String, String> entry : answers.entrySet()) {
                try {
                    Long qId = Long.parseLong(entry.getKey());
                    String selected = entry.getValue();

                    AptitudeQuestion q = questionRepo.findById(qId).orElse(null);
                    if (q != null) {
                        boolean isCorrect = q.getCorrectOption() != null
                                && q.getCorrectOption().equalsIgnoreCase(selected);
                        if (isCorrect)
                            score++;

                        details.put(qId.toString(), Map.of(
                                "correctAnswer", q.getCorrectOption() != null ? q.getCorrectOption() : "",
                                "explanation",
                                q.getExplanation() != null ? q.getExplanation() : "No explanation available.",
                                "selected", selected != null ? selected : "Unanswered",
                                "isCorrect", isCorrect));
                    }
                } catch (Exception e) {
                    System.out.println("Exception parsing question ID: " + entry.getKey());
                }
            }
        }

        // Record strictly in the relational DB historical log
        AptitudeScore newScore = new AptitudeScore(username, category, score, total);
        scoreRepo.save(newScore);

        return ResponseEntity.ok(Map.of(
                "score", score,
                "total", total,
                "percentage", total > 0 ? ((double) score / total * 100) : 0,
                "review", details));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<AptitudeScore>> getLeaderboard() {
        return ResponseEntity.ok(scoreRepo.findTop10ByOrderByScoreDesc());
    }
}
