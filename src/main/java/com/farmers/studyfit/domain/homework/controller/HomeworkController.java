package com.farmers.studyfit.domain.homework.controller;

import com.farmers.studyfit.domain.homework.dto.AssignHomeworkRequestDto;
import com.farmers.studyfit.domain.homework.service.HomeworkService;
import com.farmers.studyfit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import static com.farmers.studyfit.response.Message.*;



@RestController
@RequestMapping("/homework")
@RequiredArgsConstructor
public class HomeworkController {
    private final HomeworkService homeworkService;

    @PostMapping("/assign")
    public Response assignHomework(@RequestBody AssignHomeworkRequestDto assignHomeworkRequestDto) {
        homeworkService.assignHomework(assignHomeworkRequestDto);
        return Response.success(ASSIGN_HOMEWORK_SUCCESS);
    }
}
