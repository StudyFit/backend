package com.farmers.studyfit.domain.calendar.controller;

import com.farmers.studyfit.domain.calendar.service.CalendarService;
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

import static com.farmers.studyfit.response.Message.*;


@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {
    CalendarService calendarService;
    @GetMapping("/schedule")
    public Response getCalendarSchedule(
            @RequestParam("role") String role,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        if(role.equals("TEACHER")){
            return Response.success(GET_CALENDAR_CLASS,calendarService.getTeacherCalendarSchedule(startDate, endDate));
        }else if(role.equals("STUDENT")){
            return Response.success(GET_CALENDAR_CLASS,calendarService.getStudentCalendarSchedule(startDate, endDate));
        }else throw new CustomException(ErrorCode.INVALID_ROLE);

    }

    @GetMapping("/homework")
    public Response getCalendarHomework(
            @RequestParam("role") String role,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        if(role.equals("TEACHER")){
            return Response.success(GET_CALENDAR_HOMEWORK,calendarService.getTeacherCalendarHomework(startDate, endDate));
        }else if (role.equals("STUDENT")){
            return Response.success(GET_CALENDAR_HOMEWORK,calendarService.getStudentCalendarHomework(startDate, endDate));
        } else throw new CustomException(ErrorCode.INVALID_ROLE);
    }
}
