package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionName;
    private String userEmail; 

    @Column(columnDefinition="TEXT")
    private String code;
    
    private String language;
    
    @Column(columnDefinition="TEXT")
    private String output;
    
    private String status;

    public Submission() {}

    public Long getId() { return id; }
    
    public String getQuestionName() { return questionName; }
    public void setQuestionName(String questionName) { this.questionName = questionName; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
