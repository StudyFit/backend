package com.farmers.studyfit.domain.member.controller;

import com.farmers.studyfit.domain.member.dto.*;
import com.farmers.studyfit.domain.member.service.AuthService;
import com.farmers.studyfit.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.farmers.studyfit.response.Message.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup/student")
    public Response signUpStudent(
            @Valid @RequestBody StudentSignUpRequestDto dto) {
        authService.signUpStudent(dto);
        return Response.success(SIGNUP_SUCCESS);
    }

    @PostMapping("/signup/teacher")
    public Response signUpTeacher(
            @Valid @RequestBody TeacherSignUpRequestDto dto) {
        authService.signUpTeacher(dto);
        return Response.success(SIGNUP_SUCCESS);
    }

    @PostMapping("/login")
    public Response login(
            @Valid @RequestBody LoginRequestDto dto) {
        TokenResponseDto tokens = authService.login(dto);
        return Response.success(LOGIN_SUCCESS, tokens);
    }

    @PostMapping("/logout")
    public Response logout(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return Response.failure(HttpStatus.BAD_REQUEST, "토큰 없음");
        }
        String accessToken = header.substring(7);
        authService.logout(accessToken);
        return Response.success(LOGOUT_SUCCESS);
    }

    @PostMapping("/refresh")
    public Response refresh(@RequestBody @Valid AccessTokenRequestDto dto) {
        TokenResponseDto tokens = authService.refreshAccessToken(dto.getRefreshToken());
        return Response.success(TOKEN_SUCCESS, tokens);
    }

    @GetMapping("/test")
    public ResponseEntity<Void> test(){
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/release-test")
    public ResponseEntity<String> releaseTest(){
        return ResponseEntity.ok("배포 확인");
    }
}