package com.farmers.studyfit.domain.member.controller;

import com.farmers.studyfit.domain.member.dto.ChangePasswordRequestDto;
import com.farmers.studyfit.domain.member.dto.ProfileResponseDto;
import com.farmers.studyfit.domain.member.dto.UpdateProfileRequestDto;
import com.farmers.studyfit.domain.member.service.MemberService;
import com.farmers.studyfit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.farmers.studyfit.response.Message.*;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    
    private final MemberService memberService;

    @GetMapping("/profile")
    public Response getProfile() {
        ProfileResponseDto profile = memberService.getProfile();
        return Response.success(GET_PROFILE_SUCCESS, profile);
    }

    @PatchMapping("/profile")
    public Response updateProfile(@RequestBody UpdateProfileRequestDto requestDto) {
        memberService.updateProfile(requestDto);
        return Response.success(UPDATE_PROFILE_SUCCESS);
    }

    @PatchMapping(value = "/profile-image", consumes = "multipart/form-data")
    public Response uploadProfileImage(@RequestPart("file") MultipartFile file) throws IOException {
        String imageUrl = memberService.uploadProfileImage(file);
        return Response.success(UPLOAD_PROFILE_IMAGE_SUCCESS, imageUrl);
    }

    @PatchMapping("/password")
    public Response changePassword(@RequestBody ChangePasswordRequestDto requestDto) {
        memberService.changePassword(requestDto);
        return Response.success(CHANGE_PASSWORD_SUCCESS);
    }
}