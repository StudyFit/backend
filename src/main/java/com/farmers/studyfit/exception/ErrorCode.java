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
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    DUPLICATE_LOGIN_EXCEPTION(HttpStatus.BAD_REQUEST, "중복된 아이디 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호가 올바르지 않습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST,"역할이 TEACHER 혹은 STUDENT가 아닙니다."),
    CONNECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "연결을 찾을 수 없습니다."),
    HOMEWORK_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "날짜를 찾을 수 없습니다."),
    HOMEWORK_NOT_FOUND(HttpStatus.NOT_FOUND, "숙제를 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.NOT_FOUND, "권한이 거부되었습니다."),
    CALENDAR_NOT_FOUND(HttpStatus.NOT_FOUND, "달력 일정을 찾을 수 없습니다."),
    CANNOT_UPLOAD_IMG(HttpStatus.BAD_REQUEST, "사진 업로드 실패하였습니다."),
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "피드백을 찾을 수 없습니다."),
    PHOTO_REQUIRED_FOR_HOMEWORK(HttpStatus.BAD_REQUEST, "이 숙제는 사진 제출이 필수입니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 채팅방입니다."),
    
    INVALIDE_DATE(HttpStatus.BAD_REQUEST, "잘못된 날짜 형식입니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림 정보를 찾을 수 없습니다."),
    FCM_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Fcm 토큰을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
