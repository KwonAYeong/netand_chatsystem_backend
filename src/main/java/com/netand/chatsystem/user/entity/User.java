package com.netand.chatsystem.user.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.netand.chatsystem.common.BaseTimeEntity;
import com.netand.chatsystem.user.dto.ProfileUpdateReqDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String password;

    private String profileImageUrl;

    private String company;

    private String position;

    private boolean isActive;

    //==프로필 변경 메서드==//
    public void updateProfile(String name, String company, String position) {
        this.name = name;
        this.company = company;
        this.position = position;
    }

    //==프로필사진 변경 메서드==//
    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

}