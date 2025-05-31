package com.farmers.studyfit.domain.calender.repository;

import com.farmers.studyfit.domain.calender.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
}
