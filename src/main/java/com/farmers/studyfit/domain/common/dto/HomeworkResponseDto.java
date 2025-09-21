package com.farmers.studyfit.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkResponseDto {

    private Long homeworkId;
    private String content;
    private Boolean isCompleted;
    private Boolean isPhotoRequired;
    private Boolean isPhotoUploaded;
}
