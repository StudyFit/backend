package com.farmers.studyfit.domain.common;

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
    private String name;
    private String grade; // 학생이 조회할 경우 null
    private String subject;
    private Boolean isAllCompleted;
    private String feedback;
    private List<HomeworkResponseDto> homeworkList;
}
