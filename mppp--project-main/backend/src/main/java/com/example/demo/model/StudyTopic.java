package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "study_topics")
public class StudyTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String module; // "core-cs", "system-design", "oops"

    @Column(nullable = false)
    private String subject; // e.g., "OS", "DBMS", "Creational Patterns"

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content; // Markdown / HTML content of the topic

    @Column(columnDefinition = "TEXT")
    private String codeExample; // Optional code snippet

    @Column
    private String difficulty; // "Beginner", "Intermediate", "Advanced"

    @Column
    private String tags; // comma-separated

    public StudyTopic() {
    }

    public StudyTopic(String module, String subject, String title, String content, String codeExample,
            String difficulty, String tags) {
        this.module = module;
        this.subject = subject;
        this.title = title;
        this.content = content;
        this.codeExample = codeExample;
        this.difficulty = difficulty;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public String getModule() {
        return module;
    }

    public String getSubject() {
        return subject;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCodeExample() {
        return codeExample;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getTags() {
        return tags;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCodeExample(String codeExample) {
        this.codeExample = codeExample;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
