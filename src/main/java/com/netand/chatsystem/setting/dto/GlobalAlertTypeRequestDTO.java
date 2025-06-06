package com.netand.chatsystem.setting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
public class GlobalAlertTypeRequestDTO {
    private Long userId;
    private String alertType; // "ALL", "MENTION_ONLY", "NONE"
}
