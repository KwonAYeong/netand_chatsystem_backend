package com.netand.chatsystem.chat.repository;

import com.netand.chatsystem.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
            SELECT cr FROM ChatRoom cr
            JOIN ChatRoomParticipant p1 ON p1.chatRoom = cr AND p1.user.id = userId1
            JOIN ChatRoomParticipant p2 ON p2.chatRoom = cr AND p2.user.id = userId2
            WHERE cr.chatRoomType = 'DM'
    """)
    ChatRoom findExistingDmRoom(Long userId, Long userId2);
}
