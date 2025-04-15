package com.farmers.studyfit.domain.member.repository;

import com.farmers.studyfit.domain.member.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
