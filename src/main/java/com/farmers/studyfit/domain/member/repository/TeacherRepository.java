package com.farmers.studyfit.domain.member.repository;

import com.farmers.studyfit.domain.member.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}
