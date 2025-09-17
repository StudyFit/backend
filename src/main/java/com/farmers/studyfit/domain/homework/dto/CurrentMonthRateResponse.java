package com.farmers.studyfit.domain.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentMonthRateResponse {
    private Long connectionId;
    private int year;
    private int month;
    private long total;
    private long completed;
    private double rate;
    private String status; //"HAS_HOMEWORK" | "NO_HOMEWORK"
}
