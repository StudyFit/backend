package com.farmers.studyfit.domain.homework.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkRequestDto {
    @NotNull @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate date;
    @NotBlank
    private String content;
    private boolean isPhotoRequired;
}