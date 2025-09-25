package com.farmers.studyfit.domain.calendar.repository;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.calendar.entity.ScheduleType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    @EntityGraph(attributePaths = {"connection", "connection.teacher", "connection.student"})
    @Query("SELECT c FROM Calendar c WHERE c.date BETWEEN :startDate AND :endDate AND c.student.id = :studentId")
    List<Calendar> findByDateBetweenAndStudentId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("studentId") Long studentId);
    
    @EntityGraph(attributePaths = {"connection", "connection.teacher", "connection.student"})
    @Query("SELECT c FROM Calendar c WHERE c.date BETWEEN :startDate AND :endDate AND c.teacher.id = :teacherId")
    List<Calendar> findByDateBetweenAndTeacherId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("teacherId") Long teacherId);

    @EntityGraph(attributePaths = {"connection", "connection.teacher", "connection.student"})
    @Query("SELECT c FROM Calendar c WHERE c.date = :date AND c.student.id = :studentId AND c.scheduleType = :scheduleType")
    List<Calendar> findByDateAndStudentIdAndScheduleType(@Param("date") LocalDate date, @Param("studentId") Long studentId, @Param("scheduleType") ScheduleType scheduleType);
    
    @EntityGraph(attributePaths = {"connection", "connection.teacher", "connection.student"})
    @Query("SELECT c FROM Calendar c WHERE c.date = :date AND c.teacher.id = :teacherId AND c.scheduleType = :scheduleType")
    List<Calendar> findByDateAndTeacherIdAndScheduleType(@Param("date") LocalDate date, @Param("teacherId") Long teacherId, @Param("scheduleType") ScheduleType scheduleType);
}
