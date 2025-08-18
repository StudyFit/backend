package com.farmers.studyfit.domain.connection.repository;

import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.connection.entity.ConnectionState;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    List<Connection> findByTeacherId(Long teacherId);
    List<Connection> findByStudentId(Long studentId);
    Optional<Connection> findByStudentIdAndTeacherId(Long studentId, Long teacherId);
}
