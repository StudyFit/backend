package com.farmers.studyfit.domain.calender.repository;

import com.farmers.studyfit.domain.calender.entity.Calender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalenderRepository extends JpaRepository<Calender, Long> {
}
