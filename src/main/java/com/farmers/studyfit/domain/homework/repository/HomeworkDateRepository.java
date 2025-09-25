package com.farmers.studyfit.domain.homework.repository;

import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HomeworkDateRepository extends JpaRepository<HomeworkDate, Long> {
    Optional<HomeworkDate> findByConnection_IdAndDate(Long connectionId, LocalDate date);
    
    @EntityGraph(attributePaths = {"connection", "connection.teacher", "connection.student", "homeworkList", "homeworkList.photoList"})
    @Query("SELECT hd FROM HomeworkDate hd WHERE hd.date BETWEEN :startDate AND :endDate AND hd.teacher.id = :teacherId")
    List<HomeworkDate> findByDateBetweenAndTeacherId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("teacherId") Long teacherId);
    
    @EntityGraph(attributePaths = {"connection", "connection.teacher", "connection.student", "homeworkList", "homeworkList.photoList"})
    @Query("SELECT hd FROM HomeworkDate hd WHERE hd.date BETWEEN :startDate AND :endDate AND hd.student.id = :studentId")
    List<HomeworkDate> findByDateBetweenAndStudentId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("studentId") Long studentId);
    
    @EntityGraph(attributePaths = {"connection", "connection.teacher", "connection.student", "homeworkList", "homeworkList.photoList"})
    @Query("SELECT hd FROM HomeworkDate hd WHERE hd.connection.id = :connectionId")
    List<HomeworkDate> findByConnectionId(@Param("connectionId") Long connectionId);
    
    @EntityGraph(attributePaths = {"connection", "connection.teacher", "connection.student", "homeworkList", "homeworkList.photoList"})
    @Query("SELECT hd FROM HomeworkDate hd WHERE hd.date = :date AND hd.teacher.id = :teacherId")
    List<HomeworkDate> findByDateAndTeacherId(@Param("date") LocalDate date, @Param("teacherId") Long teacherId);
}
