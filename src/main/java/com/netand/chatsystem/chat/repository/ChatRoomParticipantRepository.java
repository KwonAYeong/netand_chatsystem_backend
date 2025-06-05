package com.netand.chatsystem.chat.repository;

import com.netand.chatsystem.chat.dto.ChatRoomListResponseDTO;
import com.netand.chatsystem.chat.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    @Query("""
        SELECT new com.netand.chatsystem.chat.dto.ChatRoomListResponseDTO(
            cr.id,
            otherUser.name,
            cr.chatRoomType,
            otherUser.profileImageUrl
        )
        FROM ChatRoomParticipant me
        JOIN me.chatRoom cr
        JOIN ChatRoomParticipant other ON other.chatRoom = cr AND other.user != me.user
        JOIN other.user otherUser
        WHERE me.user.id = :userId
        AND cr.chatRoomType = 'DM'
    """)
    List<ChatRoomListResponseDTO> findChatRoomsByUserId(Long userId);


}
