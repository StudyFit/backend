package com.farmers.studyfit.domain.calendar.repository;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
}
