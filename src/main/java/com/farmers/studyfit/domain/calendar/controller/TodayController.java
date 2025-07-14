package com.farmers.studyfit.domain.calendar.controller;

import com.farmers.studyfit.domain.calendar.service.CalendarService;
import com.farmers.studyfit.domain.calendar.service.TodayService;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import com.farmers.studyfit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.farmers.studyfit.response.Message.GET_TODAY_CLASS;

@RestController
@RequestMapping("/todayclass")
@RequiredArgsConstructor
public class TodayController {
    private final TodayService todayService;

    @GetMapping
    public Response getTodayClass(
            @RequestParam("role") String role,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        if(role.equals("TEACHER")){
            return Response.success(GET_TODAY_CLASS,todayService.getTeacherTodayClass(date));
        }else if(role.equals("STUDENT")){
            return Response.success(GET_TODAY_CLASS,todayService.getStudentTodayClass(date));
        }else throw new CustomException(ErrorCode.INVALID_ROLE);
    }
}
