package com.netand.chatsystem.user.service;

import com.netand.chatsystem.user.dto.ProfileResDTO;
import com.netand.chatsystem.user.dto.ProfileUpdateReqDTO;
import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileServiceImpl implements UserProfileService{
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileResDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        return ProfileResDTO.from(user);
    }

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
}
