package com.farmers.studyfit.domain.member.controller;

import com.farmers.studyfit.domain.member.service.MemberService;
import com.farmers.studyfit.response.Message;
import com.farmers.studyfit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PatchMapping("/student/profile-img")
    public Response uploadStudentProfileImg(@RequestPart("file") MultipartFile file) throws IOException {
        String url = memberService.uploadStudentProfileImg(file);
        return Response.success(Message.UPLOAD_PROFILE_IMG, url);
    }

    @PatchMapping("/teacher/profile-img")
    public Response uploadTeacherProfileImg(@RequestPart("file") MultipartFile file) throws IOException {
        String url = memberService.uploadTeacherProfileImg(file);
        return Response.success(Message.UPLOAD_PROFILE_IMG, url);
    }
}
