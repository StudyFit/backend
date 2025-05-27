package com.farmers.studyfit.domain.homework.repository;

import com.farmers.studyfit.domain.homework.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
