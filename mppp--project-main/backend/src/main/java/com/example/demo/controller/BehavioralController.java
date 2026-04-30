package com.example.demo.controller;

import com.example.demo.model.BehavioralQuestion;
import com.example.demo.repository.BehavioralQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/behavioral")
@CrossOrigin(origins = "*")
public class BehavioralController {

    @Autowired
    private BehavioralQuestionRepository repo;

    @GetMapping("/questions")
    public ResponseEntity<List<BehavioralQuestion>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {

        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(repo.findByCategory(category));
        }
        if (difficulty != null && !difficulty.isBlank()) {
            return ResponseEntity.ok(repo.findByDifficulty(difficulty));
        }
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
