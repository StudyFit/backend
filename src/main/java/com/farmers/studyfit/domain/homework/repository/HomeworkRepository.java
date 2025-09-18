package com.farmers.studyfit.domain.homework.repository;

import com.farmers.studyfit.domain.homework.entity.Homework;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    // (포함) 이번 달 범위의 특정 connection 모든 숙제 조회
    // startDate <= hd.date <= endDate
    @Query("""
       select h from Homework h
       join fetch h.homeworkDate hd
       where hd.connection.id = :connectionId
         and hd.date between :startDate and :endDate
    """)
    List<Homework> findAllWithDateByConnectionAndRange(
            @Param("connectionId") Long connectionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}