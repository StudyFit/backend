package com.farmers.studyfit.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkDateResponseDto {

    private Long connectionId;
    private Long homeworkDateId;
    private LocalDate date;
    private String teacherName;
    private String studentName;
    private String grade;
    private String subject;
    private Boolean isAllChecked;
    private String feedback;
    private List<HomeworkResponseDto> homeworkList;
}
