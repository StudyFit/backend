package com.farmers.studyfit.domain.connection.dto;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetColorRequestDto {
    private Long connectionId;
    private String themeColor;
}