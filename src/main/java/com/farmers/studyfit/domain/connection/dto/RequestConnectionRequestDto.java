package com.farmers.studyfit.domain.connection.dto;

import com.farmers.studyfit.domain.connection.entity.ClassTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestConnectionRequestDto {
    private Long studentId;
    private String subject;
    private String themeColor;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<ClassTimeDto> classTimeDtoList;
    private String address;
    private String memo;
}
