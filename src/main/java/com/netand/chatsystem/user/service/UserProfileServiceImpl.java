package com.netand.chatsystem.user.service;

import com.netand.chatsystem.common.s3.S3Uploader;
import com.netand.chatsystem.user.dto.ProfileResDTO;
import com.netand.chatsystem.user.dto.ProfileUpdateReqDTO;
import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileServiceImpl implements UserProfileService{
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    // 프로필 정보 조회
    @Override
    @Transactional(readOnly = true)
    public ProfileResDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        return ProfileResDTO.from(user);
    }

    // 프로필 업데이트
    @Override
    public ProfileResDTO updateProfile(Long userId, ProfileUpdateReqDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        user.updateProfile(
                dto.getName(),
                dto.getCompany(),
                dto.getPosition()
        );

        return ProfileResDTO.from(user);
    }

    // 프로필 이미지 수정
    @Override
    public String updateProfileImage(Long userId, MultipartFile imageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        // 기존 이미지가 있다면 삭제
        if (user.getProfileImageUrl() != null) {
            s3Uploader.deleteFile(user.getProfileImageUrl());
        }

        // 새 이미지 업로드
        String uploadedURL = s3Uploader.uploadFile(imageFile, "user");

        user.updateProfileImageUrl(uploadedURL);

        return uploadedURL;
    }

    // 프로필 이미지 삭제
    @Override
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        // 기존 이미지가 있다면 삭제
        if (user.getProfileImageUrl() != null) {
            s3Uploader.deleteFile(user.getProfileImageUrl());
        }

        user.updateProfileImageUrl(null);
    }

}
