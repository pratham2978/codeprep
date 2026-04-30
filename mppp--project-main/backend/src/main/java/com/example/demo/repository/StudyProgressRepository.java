package com.example.demo.repository;

import com.example.demo.model.StudyProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyProgressRepository extends JpaRepository<StudyProgress, Long> {
    List<StudyProgress> findByUsername(String username);

    Optional<StudyProgress> findByUsernameAndTopicId(String username, Long topicId);

    long countByUsernameAndCompleted(String username, boolean completed);
}
