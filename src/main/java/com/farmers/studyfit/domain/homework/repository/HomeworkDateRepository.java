package com.farmers.studyfit.domain.homework.repository;

import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkDateRepository extends JpaRepository<HomeworkDate, Long> {
}
