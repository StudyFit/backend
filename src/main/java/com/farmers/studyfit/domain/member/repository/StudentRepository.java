package com.farmers.studyfit.domain.member.repository;

import com.farmers.studyfit.domain.member.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
