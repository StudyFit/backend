package com.farmers.studyfit.domain.homework.controller;

import com.farmers.studyfit.domain.common.dto.HomeworkDateResponseDto;
import com.farmers.studyfit.domain.homework.dto.CurrentMonthRateResponse;
import com.farmers.studyfit.domain.homework.dto.PostFeedbackRequestDto;
import com.farmers.studyfit.domain.homework.dto.HomeworkRequestDto;
import com.farmers.studyfit.domain.homework.dto.CheckHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.service.HomeworkService;
import com.farmers.studyfit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.farmers.studyfit.response.Message.*;



@RestController
@RequestMapping("/homeworks")
@RequiredArgsConstructor
public class HomeworkController {
    private final HomeworkService homeworkService;

    //(선생님) 숙제 등록
    @PostMapping("/{connectionId}")
    public Response assignHomework(@PathVariable("connectionId") Long connectionId, @RequestBody HomeworkRequestDto homeworkRequestDto) {
        homeworkService.postHomework(connectionId, homeworkRequestDto);
        return Response.success(POST_HOMEWORK_SUCCESS);
    }

    // 숙제 수정하기
    @PatchMapping("/{connectionId}")
    public Response postHomework(@PathVariable("connectionId") Long connectionId, @RequestBody HomeworkRequestDto homeworkRequestDto) {
        homeworkService.patchHomework(connectionId, homeworkRequestDto);
        return Response.success(PATCH_HOMEWORK_SUCCESS);
    }

    @DeleteMapping("/{homeworkDateId}")
    public Response deleteHomework(@PathVariable("homeworkDateId") Long homeworkDateId) {
        homeworkService.deleteHomework(homeworkDateId);
        return Response.success(DELETE_HOMEWORK_SUCCESS);
    }

    //(선생님) 피드백 등록 및 수정하기
    @PatchMapping("/{homeworkDateId}/feedback")
    public Response postFeedback(@PathVariable("homeworkDateId") Long homeworkDateId, @RequestBody PostFeedbackRequestDto postFeedbackRequestDto) {
        homeworkService.postFeedback(homeworkDateId, postFeedbackRequestDto);
        return Response.success(POST_FEEDBACK_SUCCESS);
    }

    // 피드백 삭제하기
    @DeleteMapping("/{homeworkDateId}/feedback")
    public Response deleteFeedback(@PathVariable("homeworkDateId") Long homeworkDateId) {
        homeworkService.deleteFeedback(homeworkDateId);
        return Response.success(DELETE_FEEDBACK_SUCCESS);
    }

    @PatchMapping(value = "/{homeworkId}/check", consumes = "multipart/form-data")
    public Response checkHomework(@PathVariable("homeworkId") Long homeworkId, 
                                 @RequestParam("isChecked") boolean isChecked,
                                 @RequestParam(value = "photo", required = false) MultipartFile photo) {
        CheckHomeworkRequestDto checkHomeworkRequestDto = new CheckHomeworkRequestDto(isChecked, photo);
        String uploadedImageUrl = homeworkService.checkHomework(homeworkId, checkHomeworkRequestDto);
        return Response.success(CHECK_HOMEWORK_SUCCESS, uploadedImageUrl);
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

    //이번 달 달성률 불러오기
    @GetMapping("/rate/{connectionId}")
    public Response getCurrentMonthRateofHomework(@PathVariable("connectionId") Long connectionId) {
        CurrentMonthRateResponse currentMonthRateDto = homeworkService.getCurrentMonthRate(connectionId);
        return Response.success(GET_CURRENT_MONTH_RATE_SUCCESS, currentMonthRateDto);
    }
}