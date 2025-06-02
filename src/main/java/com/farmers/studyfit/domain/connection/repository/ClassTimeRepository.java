package com.farmers.studyfit.domain.connection.repository;

import com.farmers.studyfit.domain.connection.entity.ClassTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassTimeRepository extends JpaRepository<ClassTime, Long> {
    List<ClassTime> findByConnectionId(Long connectionId);
}
