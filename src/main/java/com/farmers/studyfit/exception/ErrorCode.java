package com.farmers.studyfit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "만료된 엑세스 토큰입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 엑세스 토큰입니다."),
    INVALID_REFRESH_TOCKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOCKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    DUPLICATE_LOGIN_EXCEPTION(HttpStatus.BAD_REQUEST, "중복된 아이디 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호가 올바르지 않습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST,"역할이 TEACHER 혹은 STUDENT가 아닙니다."),
    CONNECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "연결을 찾을 수 없습니다."),
    CALENDAR_NOT_FOUND(HttpStatus.NOT_FOUND, "달력 일정을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
