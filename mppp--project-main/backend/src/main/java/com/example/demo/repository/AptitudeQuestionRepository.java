package com.example.demo.repository;

import com.example.demo.model.AptitudeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AptitudeQuestionRepository extends JpaRepository<AptitudeQuestion, Long> {
    List<AptitudeQuestion> findByCategory(String category);

    // Natively fetches random questions optimizing memory latency
    @Query(value = "SELECT * FROM aptitude_questions WHERE category = ?1 ORDER BY RAND() LIMIT ?2", nativeQuery = true)
    List<AptitudeQuestion> findRandomByCategory(String category, int limit);
}
