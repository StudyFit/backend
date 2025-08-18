package com.farmers.studyfit.domain.homework.dto;

import com.farmers.studyfit.domain.homework.entity.Feedback;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignFeedbackRequestDto {
    private String feedback;
}