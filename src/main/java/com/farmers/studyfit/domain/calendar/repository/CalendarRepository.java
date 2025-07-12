package com.farmers.studyfit.domain.calendar.repository;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    List<Calendar> findByDateBetweenAndStudentId(LocalDate startDate, LocalDate endDate, Long studentId);
    List<Calendar> findByDateBetweenAndTeacherId(LocalDate startDate, LocalDate endDate, Long teacherId);
}
