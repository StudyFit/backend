package com.farmers.studyfit.domain.homework.repository;

import com.farmers.studyfit.domain.homework.entity.HomeworkPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkPhotoRepository extends JpaRepository<HomeworkPhoto, Long> {
    
    List<HomeworkPhoto> findByHomeworkId(Long homeworkId);
    long countByHomeworkId(Long homeworkId);
}
