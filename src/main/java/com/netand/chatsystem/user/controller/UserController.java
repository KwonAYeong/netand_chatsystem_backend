package com.netand.chatsystem.user.controller;

import com.netand.chatsystem.common.websocket.UserSessionManager;
import com.netand.chatsystem.user.dto.ProfileResDTO;
import com.netand.chatsystem.user.dto.ProfileUpdateReqDTO;
import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import com.netand.chatsystem.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;
    private final UserRepository userRepository;
    private final UserSessionManager userSessionManager;

    // 유저 프로필 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResDTO> getUserProfile(
            @PathVariable Long userId
    ) {
        ProfileResDTO resDTO = userProfileService.getProfile(userId);
        return ResponseEntity.ok(resDTO);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ProfileResDTO> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody ProfileUpdateReqDTO dto
    ) {
        ProfileResDTO resDTO = userProfileService.updateProfile(userId, dto);
        return ResponseEntity.ok(resDTO);
    }

    @PostMapping("/{userId}/profile-image")
    public ResponseEntity<String> updateProfileImage(
            @PathVariable Long userId,
            @RequestPart("image")MultipartFile imageFile
            ) {
        String imgURL = userProfileService.updateProfileImage(userId, imageFile);
        return ResponseEntity.ok(imgURL);
    }

    @DeleteMapping("/{userId}/profile-image")
    public ResponseEntity<Void> deleteProfileImage(
            @PathVariable Long userId
    ) {
        userProfileService.deleteProfileImage(userId);
        return ResponseEntity.noContent().build();
    }

    // 유저 접속 상태 api
    // 웹 시작 시 초기 상태값 확인용
    @GetMapping("/api/status/{userId}")
    public ResponseEntity<String> getUserStatus(
            @PathVariable Long userId
    ) {
        return userRepository.findById(userId)
                .map(user -> {
                    boolean isSettingOnline = user.isActive(); // 설정된 상태
                    System.out.println("UserController.getUserStatus");
                    boolean isConnected = userSessionManager.isOnline(user.getId()); // 현재 접속 상태
                    boolean shouldBeOnline = isSettingOnline && isConnected;
                    return shouldBeOnline ? "ONLINE" : "AWAY";
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
