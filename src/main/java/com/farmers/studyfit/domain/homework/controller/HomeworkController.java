package com.farmers.studyfit.domain.homework.controller;

import com.farmers.studyfit.domain.common.dto.HomeworkDateResponseDto;
import com.farmers.studyfit.domain.homework.dto.AssignFeedbackRequestDto;
import com.farmers.studyfit.domain.homework.dto.AssignHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.dto.CheckHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import com.farmers.studyfit.domain.homework.service.HomeworkService;
import com.farmers.studyfit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.farmers.studyfit.response.Message.*;



@RestController
@RequestMapping("/homeworks")
@RequiredArgsConstructor
public class HomeworkController {
    private final HomeworkService homeworkService;

    //(선생님) 숙제 등록
    @PostMapping("/{connectionId}")
    public Response assignHomework(@PathVariable("connectionId") Long connectionId, @RequestBody AssignHomeworkRequestDto assignHomeworkRequestDto) {
        homeworkService.assignHomework(connectionId, assignHomeworkRequestDto);
        return Response.success(ASSIGN_HOMEWORK_SUCCESS);
    }

    //(선생님) 피드백 등록
    @PatchMapping("/{homeworkDateId}/feedback")
    public Response assignFeedback(@PathVariable("homeworkDateId") Long homeworkDateId, @RequestBody AssignFeedbackRequestDto assignFeedbackRequestDto) {
        homeworkService.assignFeedback(homeworkDateId, assignFeedbackRequestDto);
        return Response.success(ASSIGN_FEEDBACK_SUCCESS);
    }

    //(학생) 숙제 체크하기
    @PostMapping("/{homeworkId}/check")
    public Response checkHomework(@PathVariable("homeworkId") Long homeworkId, @RequestBody CheckHomeworkRequestDto checkHomeworkRequestDto) {
        homeworkService.checkHomework(homeworkId, checkHomeworkRequestDto);
        return Response.success(CHECK_HOMEWORK_SUCCESS);
    }

    //숙제 목록 불러오기(학생별)
    @GetMapping("/{studentId}/homeworkListByStudent")
    public Response getHomeworkListByStudent(@PathVariable("studentId") Long studentId) {
        List<HomeworkDateResponseDto> homeworkDateResponseDtoList = homeworkService.getHomeworkListByStudent(studentId);
        return Response.success(GET_HOMEWORK_LIST_BY_STUDENT_SUCCESS,
                homeworkDateResponseDtoList);
    }

    //숙제 목록 불러오기(날짜별)
    @GetMapping("/{homeworkDateId}/homeworkListByDate")
    public Response getHomeworkListByDate(@PathVariable("homeworkDateId") Long homeworkDateId) {
        List<HomeworkDateResponseDto> homeworkDateResponseDtoList = homeworkService.getHomeworkListByDate(homeworkDateId);
        return Response.success(GET_HOMEWORK_LIST_BY_DATE_SUCCESS,
                homeworkDateResponseDtoList);
    }
}