package com.farmers.studyfit.domain.connection.controller;

import com.farmers.studyfit.domain.connection.dto.*;
import com.farmers.studyfit.domain.connection.service.ConnectionService;
import com.farmers.studyfit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.farmers.studyfit.response.Message.*;

@RestController
@RequestMapping("/connection")
@RequiredArgsConstructor
public class ConnectionController {
    private final ConnectionService connectionService;

    @GetMapping("/search")
    public Response searchStudent(@RequestParam("loginId") String loginId) {
        SearchStudentResponseDto studentInfo = connectionService.findStudentByLoginId(loginId);
        return Response.success(SEARCH_STUDENT_SUCCESS, studentInfo);
    }

    @PatchMapping("/request")
    public Response requestConnection(@RequestBody RequestConnectionRequestDto requestConnectionRequestDto) {
        connectionService.requestConnection(requestConnectionRequestDto);
        return Response.success(REQUEST_CONNECTION_SUCCESS);
    }

    @PostMapping("/response")
    public Response responseConnection(@RequestBody ResponseConnectionRequestDto responseConnectionRequestDto) {
        if (responseConnectionRequestDto.getAction().equals("ACCEPTED")) {
            connectionService.acceptConnection(responseConnectionRequestDto.getConnectionId());
            return Response.success(ACCEPT_CONNECTION_SUCCESS);
        } else {
            connectionService.rejectConnection(responseConnectionRequestDto.getConnectionId());
            return Response.success(REJECT_CONNECTION_SUCCESS);
        }
    }

    @PatchMapping("/color")
    public Response setTeacherColor(@RequestBody SetColorRequestDto setColorRequestDto) {
        connectionService.setTeacherColor(setColorRequestDto.getConnectionId(), setColorRequestDto.getThemeColor());
        return Response.success(SET_COLOR_SUCCESS);
    }

    @GetMapping("/students")
    public Response getStudentList() {
        List<StudentDto> studentList = connectionService.getAllConnectedStudentsByTeacher();
        return Response.success(SEARCH_STUDENT_LIST_SUCCESS, studentList);
    }

    @GetMapping("/teachers")
    public Response getTeacherList() {
        List<TeacherDto> teacherList = connectionService.getAllConnectedTeachersByStudent();
        return Response.success(SEARCH_TEACHER_LIST_SUCCESS, teacherList);
    }

    @DeleteMapping("/{connectionId}")
    public Response deleteConnection(@PathVariable("connectionId") Long connectionId) {
        connectionService.deleteConnection(connectionId);
        return Response.success("");
    }
}
