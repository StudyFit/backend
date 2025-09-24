package com.farmers.studyfit.domain.common.converter;

import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DateConverter {
    public String convertDate(String dateString) {
        // 날짜 문자열이 null이거나 비어 있는지 확인
        if (dateString == null || dateString.isEmpty()) {
            throw new CustomException(ErrorCode.INVALIDE_DATE);
        }

        try {
            // "yyyy-MM-dd" 형식의 포맷터 정의
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 입력된 문자열을 LocalDate 객체로 파싱
            LocalDate date = LocalDate.parse(dateString, inputFormatter);

            // "M월 d일" 형식의 포맷터 정의
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("M월 d일");
            // LocalDate 객체를 새로운 형식의 문자열로 변환
            return date.format(outputFormatter);

        } catch (DateTimeParseException e) {
            // 파싱 중 오류가 발생하면 예외 처리
            throw new CustomException(ErrorCode.INVALIDE_DATE);
        }
    }
}
