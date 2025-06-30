package com.netand.chatsystem.chat.repository;

import com.netand.chatsystem.chat.entity.ChatMessage;
import com.netand.chatsystem.chat.entity.ChatRoom;
import com.netand.chatsystem.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @EntityGraph(attributePaths = {"sender"})
    List<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);

    int countByChatRoomAndIdGreaterThanAndSenderNot(ChatRoom chatRoom, Long id, User sender);

    ChatMessage findTopByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);

    long countByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long messageId);

    @Query(value = """
    SELECT c.*\s
    FROM chat_message c
    INNER JOIN message_interaction i ON c.id = i.message_id
    WHERE i.user_id = :userId
      AND i.interaction_type = 'MENTION'
    ORDER BY c.created_at DESC
    """, nativeQuery = true)
    List<ChatMessage> findMentionMessagesByUserId(@Param("userId") Long userId);

}
