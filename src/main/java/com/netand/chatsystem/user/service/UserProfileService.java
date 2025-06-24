package com.netand.chatsystem.user.service;

import com.netand.chatsystem.user.dto.ProfileResDTO;
import com.netand.chatsystem.user.dto.ProfileUpdateReqDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
    ProfileResDTO getProfile(Long userId);
    ProfileResDTO updateProfile(Long userId, ProfileUpdateReqDTO dto);
    String updateProfileImage(Long userId, MultipartFile imageFile);
    void deleteProfileImage(Long userId);
}
