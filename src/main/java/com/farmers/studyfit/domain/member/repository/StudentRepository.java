package com.farmers.studyfit.domain.member.repository;

import com.farmers.studyfit.domain.member.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}
