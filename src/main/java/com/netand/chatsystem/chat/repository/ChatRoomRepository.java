package com.netand.chatsystem.chat.repository;

import com.netand.chatsystem.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        SELECT cr FROM ChatRoom cr
        JOIN cr.participants p1
        JOIN cr.participants p2
        WHERE p1.user.id = :userId1
        AND p2.user.id = :userId2
        AND cr.chatRoomType = 'DM'
    """)
    ChatRoom findExistingDmRoom(Long userId1, Long userId2);
}
