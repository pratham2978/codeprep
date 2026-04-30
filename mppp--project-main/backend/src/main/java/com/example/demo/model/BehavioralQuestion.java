package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "behavioral_questions")
public class BehavioralQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category; // "teamwork", "leadership", "conflict", "achievement", "failure"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String starSample; // Sample STAR answer

    @Column
    private String difficulty; // "Common", "Tough", "Senior"

    @Column
    private String keywords; // comma-separated keywords to hit in answer

    public BehavioralQuestion() {
    }

    public BehavioralQuestion(String category, String question, String starSample, String difficulty, String keywords) {
        this.category = category;
        this.question = question;
        this.starSample = starSample;
        this.difficulty = difficulty;
        this.keywords = keywords;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getQuestion() {
        return question;
    }

    public String getStarSample() {
        return starSample;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setStarSample(String starSample) {
        this.starSample = starSample;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
