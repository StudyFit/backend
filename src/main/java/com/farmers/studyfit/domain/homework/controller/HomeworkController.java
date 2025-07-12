package com.farmers.studyfit.domain.homework.controller;

import com.farmers.studyfit.domain.homework.dto.AssignFeedbackRequestDto;
import com.farmers.studyfit.domain.homework.dto.AssignHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.dto.SubmitHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.service.HomeworkService;
import com.farmers.studyfit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.farmers.studyfit.response.Message.*;



@RestController
@RequestMapping("/homework")
@RequiredArgsConstructor
public class HomeworkController {
    private final HomeworkService homeworkService;

    //(선생님) 숙제 등록
    @PostMapping("/assign")
    public Response assignHomework(@RequestBody AssignHomeworkRequestDto assignHomeworkRequestDto) {
        homeworkService.assignHomework(assignHomeworkRequestDto);
        return Response.success(ASSIGN_HOMEWORK_SUCCESS);
    }

    //(선생님) 피드백 등록
    @PatchMapping("/feedback")
    public Response assignFeedback(@RequestBody AssignFeedbackRequestDto assignFeedbackRequestDto) {
        homeworkService.assignFeedback(assignFeedbackRequestDto);
        return Response.success(ASSIGN_FEEDBACK_SUCCESS);
    }

    //(학생) 숙제 제출
    @PostMapping("/submit")
    public Response submitHomework(@RequestBody SubmitHomeworkRequestDto submitHomeworkRequestDto) {
        homeworkService.submitHomework(submitHomeworkRequestDto);
        return Response.success(SUBMIT_HOMEWORK_SUCCESS);
    }

      //숙제 목록 불러오기(학생별)
    @GetMapping("/students")
    public Response getHomeworkListByStudent() {
        List<HomeworkDto> homeworkDtoList = homeworkService.getHomeworkListsByStudent();
        return Response.success(GET_HOMEWORK_LIST_BY_STUDENT_SUCCESS, homeworkDtoList);
    }

//    @GetMapping("/students")
//    public Response getStudentList() {
//        List<StudentDto> studentList = connectionService.getAllConnectedStudentsByTeacher();
//        return Response.success(SEARCH_STUDENT_LIST_SUCCESS, studentList);
//    }
}
