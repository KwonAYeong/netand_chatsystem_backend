package com.netand.chatsystem.user.service;

import com.netand.chatsystem.user.dto.MentionMessageResDTO;

import java.util.List;

public interface MyActivityService {

    List<MentionMessageResDTO> getMentions(Long userId);

}
