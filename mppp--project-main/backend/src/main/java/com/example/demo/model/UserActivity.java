package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity")
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private int totalSolved;

    @Column
    private int easySolved;

    @Column
    private int mediumSolved;

    @Column
    private int hardSolved;

    @Column
    private int submissions;

    @Column
    private int streakDays;

    @Column
    private int bestStreak;

    @Column
    private int totalPoints;

    @Column
    private int quizzesTaken;

    @Column
    private LocalDateTime lastActive;

    public UserActivity() {
    }

    public UserActivity(String username) {
        this.username = username;
        this.totalSolved = 0;
        this.submissions = 0;
        this.quizzesTaken = 0;
        this.totalPoints = 0;
        this.bestStreak = 0;
        this.lastActive = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getTotalSolved() {
        return totalSolved;
    }

    public int getEasySolved() {
        return easySolved;
    }

    public int getMediumSolved() {
        return mediumSolved;
    }

    public int getHardSolved() {
        return hardSolved;
    }

    public int getSubmissions() {
        return submissions;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getQuizzesTaken() {
        return quizzesTaken;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTotalSolved(int totalSolved) {
        this.totalSolved = totalSolved;
    }

    public void setEasySolved(int easySolved) {
        this.easySolved = easySolved;
    }

    public void setMediumSolved(int mediumSolved) {
        this.mediumSolved = mediumSolved;
    }

    public void setHardSolved(int hardSolved) {
        this.hardSolved = hardSolved;
    }

    public void setSubmissions(int submissions) {
        this.submissions = submissions;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void setQuizzesTaken(int quizzesTaken) {
        this.quizzesTaken = quizzesTaken;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }
}
