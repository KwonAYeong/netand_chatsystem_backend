package com.netand.chatsystem.chat.repository;

import com.netand.chatsystem.chat.entity.ChatRoom;
import com.netand.chatsystem.chat.entity.ChatRoomParticipant;
import com.netand.chatsystem.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    int countByChatRoomId(Long chatRoomId);

    int countByChatRoomIdAndLeftAtIsNull(Long chatRoomId);

    List<ChatRoomParticipant> findByChatRoomIdAndLeftAtIsNull(Long chatRoomId);

    List<ChatRoomParticipant> findByUserIdAndLeftAtIsNull(Long userId);

    void deleteByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    List<ChatRoomParticipant> findByUserId(Long userId);

    List<ChatRoomParticipant> findByChatRoomId(Long chatroomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ChatRoomParticipant p WHERE p.chatRoom.id = :chatRoomId AND p.user.id = :userId")
    Optional<ChatRoomParticipant> findWithLockByChatRoomIdAndUserId(@Param("chatRoomId") Long chatRoomId,
                                                                    @Param("userId") Long userId);

    @Query("SELECT p FROM ChatRoomParticipant p WHERE p.chatRoom.id = :chatRoomId AND p.user.id = :userId")
    Optional<ChatRoomParticipant> findByChatRoomIdAndUserId(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    @Query("SELECT crp.user.id FROM ChatRoomParticipant crp WHERE crp.chatRoom.id = :chatRoomId")
    List<Long> findUserIdsByChatRoomId(@Param("chatRoomId") Long chatRoomId);

}
