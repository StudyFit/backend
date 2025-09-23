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
    public static final String POST_HOMEWORK_SUCCESS = "숙제 등록하기 성공";
    public static final String PATCH_HOMEWORK_SUCCESS = "숙제 수정하기 성공";
    public static final String DELETE_HOMEWORK_SUCCESS = "숙제 삭제하기 성공";
    public static final String POST_FEEDBACK_SUCCESS = "피드백 등록하기 성공";
    public static final String DELETE_FEEDBACK_SUCCESS = "피드백 삭제하기 성공";
    public static final String CHECK_HOMEWORK_SUCCESS = "숙제 제출하기 성공";
    public static final String GET_HOMEWORK_LIST_BY_STUDENT_SUCCESS = "학생별 숙제 목록 불러오기 성공";
    public static final String GET_HOMEWORK_LIST_BY_DATE_SUCCESS = "날짜별 숙제 목록 불러오기 성공";

    public static final String GET_CALENDAR_CLASS = "캘린더 수업, 일정 불러오기 성공";
    public static final String GET_CALENDAR_HOMEWORK = "캘린더 숙제 불러오기 성공";

    public static final String GET_TODAY_CLASS = "오늘의 수업 불러오기 성공";
    public static final String POST_SCHEDULE = "일정 등록하기 성공";
    public static final String PATCH_SCHEDULE = "세부 수업/기타 일정 수정하기 성공";
    public static final String DELETE_SCHEDULE = "세부 수업/기타 일정 삭제하기 성공";

    public static final String GET_CURRENT_MONTH_RATE_SUCCESS = "이번 달 숙제 달성률 불러오기 성공";
    
    public static final String UPLOAD_PROFILE_IMG = "이미지 업로드하기 성공";
    public static final String GET_PROFILE_SUCCESS = "프로필 조회하기 성공";
    public static final String UPDATE_PROFILE_SUCCESS = "프로필 수정하기 성공";
    public static final String UPLOAD_PROFILE_IMAGE_SUCCESS = "프로필 이미지 업로드 성공";
    public static final String CHANGE_PASSWORD_SUCCESS = "비밀번호 변경하기 성공";

    public static final String CREATE_CHAT_ROOM_SUCCESS = "채팅방 생성하기 성공";
    public static final String GET_CHAT_ROOM_LIST_SUCCESS = "채팅방 목록 조회하기 성공";
    public static final String SEND_MESSAGE_SUCCESS = "메시지/이미지 전송하기 성공";
    public static final String GET_CHAT_MESSAGES_SUCCESS = "채팅 메시지 조회하기 성공";

    public static final String POST_ALARM = "알림 보내기 성공";
    public static final String GET_ALARM = "알림 불러오기 성공";
    public static final String MARK_ALARM = "알림 읽음 처리 성공";
    public static final String FCM_TOKEN_REGISTERED = "FCM 토큰이 성공적으로 등록되었습니다.";
}