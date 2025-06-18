package com.netand.chatsystem.user.service;

import com.netand.chatsystem.user.dto.ProfileResDTO;
import com.netand.chatsystem.user.dto.ProfileUpdateReqDTO;

public interface UserProfileService {
    ProfileResDTO getProfile(Long userId);
    ProfileResDTO updateProfile(Long userId, ProfileUpdateReqDTO dto);
}
