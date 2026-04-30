package com.example.demo.repository;

import com.example.demo.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    Optional<UserActivity> findByUsername(String username);

    List<UserActivity> findTop20ByOrderByTotalSolvedDesc();
}
