package com.farmers.studyfit.response;

public class Message {
    public static final String SIGNUP_SUCCESS = "회원가입 성공";
    public static final String LOGIN_SUCCESS = "로그인 성공";
    public static final String LOGOUT_SUCCESS = "로그아웃 성공";
    public static final String TOKEN_SUCCESS = "토큰 재발급 성공";

    public static final String SEARCH_STUDENT_SUCCESS = "학생 ID 조회하기 성공";
    public static final String REQUEST_CONNECTION_SUCCESS = "학생 정보 추가하기 성공";
    public static final String ACCEPT_CONNECTION_SUCCESS = "친구 요청 수락하기 성공";
    public static final String REJECT_CONNECTION_SUCCESS = "친구 요청 거절하기 성공";
    public static final String SET_COLOR_SUCCESS = "친구 요청 수락 후 색상 설정하기 성공";
    public static final String SEARCH_STUDENT_LIST_SUCCESS = "학생 목록 조회하기 성공";
    public static final String SEARCH_TEACHER_LIST_SUCCESS = "선생님 목록 조회하기 성공";
    public static final String ASSIGN_HOMEWORK_SUCCESS = "숙제 등록하기 성공";

    public static final String GET_CALENDAR_CLASS = "캘린더 수업, 일정 불러오기 성공";
    public static final String GET_CALENDAR_HOMEWORK = "캘린더 숙제 불러오기 성공";

    public static final String GET_TODAY_CLASS = "오늘의 수업 불러오기 성공";
    public static final String POST_SCHEDULE = "일정 등록하기 성공";
    public static final String PATCH_SCHEDULE = "세부 수업/기타 일정 수정하기 성공";
    public static final String DELETE_SCHEDULE = "세부 수업/기타 일정 삭제하기 성공";

}
