package com.netand.chatsystem.user.service;

import com.netand.chatsystem.chat.entity.ChatMessage;
import com.netand.chatsystem.chat.repository.ChatMessageRepository;
import com.netand.chatsystem.chat.repository.ChatRoomRepository;
import com.netand.chatsystem.user.dto.MentionMessageResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyActivityServiceImpl implements MyActivityService{

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public List<MentionMessageResDTO> getMentions(Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findMentionMessagesByUserId(userId);

        return messages.stream()
                .map(MentionMessageResDTO::from)
                .toList();
    }
}
