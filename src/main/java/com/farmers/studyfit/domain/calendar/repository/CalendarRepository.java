package com.farmers.studyfit.domain.calendar.repository;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.calendar.entity.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    List<Calendar> findByDateBetweenAndStudentId(LocalDate startDate, LocalDate endDate, Long studentId);
    List<Calendar> findByDateBetweenAndTeacherId(LocalDate startDate, LocalDate endDate, Long teacherId);

    List<Calendar> findByDateAndStudentIdAndScheduleType(LocalDate date, Long studentId, ScheduleType scheduleType);
    List<Calendar> findByDateAndTeacherIdAndScheduleType(LocalDate date, Long studentId, ScheduleType scheduleType);
}
