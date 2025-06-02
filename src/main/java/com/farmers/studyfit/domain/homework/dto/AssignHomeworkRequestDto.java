package com.farmers.studyfit.domain.homework.dto;

import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignHomeworkRequestDto {
    private Long connectionId;
    private LocalDate date;
    private String content;
    private boolean isCompleted;
    private boolean isPhotoRequired;
}