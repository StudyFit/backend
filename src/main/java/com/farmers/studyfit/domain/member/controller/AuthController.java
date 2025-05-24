package com.farmers.studyfit.domain.member.controller;

import com.farmers.studyfit.domain.member.dto.*;
import com.farmers.studyfit.domain.member.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup/student")
    public ResponseEntity<Void> signUpStudent(
            @Valid @RequestBody StudentSignUpRequestDto dto) {
        authService.signUpStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signup/teacher")
    public ResponseEntity<Void> signUpTeacher(
            @Valid @RequestBody TeacherSignUpRequestDto dto) {
        authService.signUpTeacher(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto) {
        TokenResponseDto tokens = authService.login(dto);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String accessToken = header.substring(7);
        authService.logout(accessToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody @Valid AccessTokenRequestDto dto) {
        TokenResponseDto tokens = authService.refreshAccessToken(dto.getRefreshToken());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/test")
    public ResponseEntity<Void> test(){
        return ResponseEntity.noContent().build();
    }
}