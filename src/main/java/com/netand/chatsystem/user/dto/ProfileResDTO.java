package com.netand.chatsystem.user.dto;

import com.netand.chatsystem.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProfileResDTO {

    private Long id;
    private String name;
    private String profileImageUrl;
    private String email;
    private String company;
    private String position;
    private boolean isActive;

    //==DTO 생성 메서드==//
    public static ProfileResDTO from(User user) {
        return ProfileResDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .email(user.getEmail())
                .company(user.getCompany())
                .position(user.getPosition())
                .isActive(user.isActive())
                .build();
    }

}
