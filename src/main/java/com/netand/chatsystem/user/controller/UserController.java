package com.netand.chatsystem.user.controller;

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

}
