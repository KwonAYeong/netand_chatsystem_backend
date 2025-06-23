package com.netand.chatsystem.chat.repository;

import com.netand.chatsystem.chat.entity.ChatMessage;
import com.netand.chatsystem.chat.entity.ChatRoom;
import com.netand.chatsystem.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @EntityGraph(attributePaths = {"sender"})
    List<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);

    int countByChatRoomAndIdGreaterThanAndSenderNot(ChatRoom chatRoom, Long id, User sender);

    ChatMessage findTopByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);

    long countByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long messageId);

}
