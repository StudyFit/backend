package com.farmers.studyfit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    INVALID_REFRESH_TOCKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOCKEN(HttpStatus.BAD_REQUEST, "리프레시 토큰이 만료되었습니다."),
    DUPLICATE_LOGIN_EXCEPTION(HttpStatus.BAD_REQUEST, "중복된 아이디 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호가 올바르지 않습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST,"TEACHER 혹은 STUDENT중 역할을 선택해주세요."),
    CONNECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "연결을 찾을 수 없습니다."),
    HOMEWORK_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "날짜를 찾을 수 없습니다."),
    HOMEWORK_NOT_FOUND(HttpStatus.NOT_FOUND, "숙제를 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.NOT_FOUND, "권한이 거부되었습니다."),
    CALENDAR_NOT_FOUND(HttpStatus.NOT_FOUND, "달력 일정을 찾을 수 없습니다."),
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "피드백을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
