package com.farmers.studyfit.domain.homework.repository;

import com.farmers.studyfit.domain.homework.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {

}