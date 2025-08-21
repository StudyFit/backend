package com.farmers.studyfit.domain.homework.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostHomeworkRequestDto {
    private LocalDate date;
    private String content;
    private boolean isPhotoRequired;
}