package com.farmers.studyfit.domain.homework.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homework_date_id")
    private HomeworkDate homeworkDate;
    private String content;
    private boolean isCompleted;
    private boolean isPhotoRequired;
}
