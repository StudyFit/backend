package com.farmers.studyfit.domain.connection.controller;

import com.farmers.studyfit.domain.connection.dto.RequestConnectionRequestDto;
import com.farmers.studyfit.domain.connection.dto.SearchStudentResponseDto;
import com.farmers.studyfit.domain.connection.service.ConnectionService;
import com.farmers.studyfit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/request")
    public Response requestConnection(@RequestBody RequestConnectionRequestDto requestConnectionRequestDto){
        connectionService.requestConnection(requestConnectionRequestDto);
        return Response.success(REQUEST_CONNECTION_SUCCESS);
    }

}
