package com.example.demo.repository;

import com.example.demo.model.StudyTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudyTopicRepository extends JpaRepository<StudyTopic, Long> {
    List<StudyTopic> findByModule(String module);

    List<StudyTopic> findByModuleAndSubject(String module, String subject);
}
