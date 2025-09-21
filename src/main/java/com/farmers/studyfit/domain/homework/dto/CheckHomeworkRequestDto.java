package com.farmers.studyfit.domain.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckHomeworkRequestDto {
    private boolean isChecked;
    private MultipartFile photo;
}