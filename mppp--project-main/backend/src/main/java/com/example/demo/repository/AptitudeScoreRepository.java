package com.example.demo.repository;

import com.example.demo.model.AptitudeScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AptitudeScoreRepository extends JpaRepository<AptitudeScore, Long> {
    List<AptitudeScore> findTop10ByOrderByScoreDesc();
}
