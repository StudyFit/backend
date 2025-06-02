package com.farmers.studyfit.domain.homework.repository;

import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface HomeworkDateRepository extends JpaRepository<HomeworkDate, Long> {
    Optional<HomeworkDate> findByConnectionIdAndDate(Long connectionId, LocalDate date);
}
