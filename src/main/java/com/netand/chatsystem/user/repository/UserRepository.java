package com.netand.chatsystem.user.repository;

import com.netand.chatsystem.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByEmailIn(Collection<String> emails);

    @Query("""
    SELECT u
    FROM ChatRoomParticipant p
    JOIN p.user u
    WHERE p.chatRoom.id = :chatRoomId
      AND u.name = :name
    """)
    Optional<User> findByNameInChatRoom(@Param("name") String name, @Param("chatRoomId") Long chatRoomId);


}

