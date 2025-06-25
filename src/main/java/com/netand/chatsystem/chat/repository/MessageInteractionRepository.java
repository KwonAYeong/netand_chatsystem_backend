package com.netand.chatsystem.chat.repository;

import com.netand.chatsystem.chat.entity.InteractionType;
import com.netand.chatsystem.chat.entity.MessageInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageInteractionRepository extends JpaRepository<MessageInteraction, Long> {

    List<MessageInteraction> findByMessageIdAndInteractionType(Long messageId, InteractionType interactionType);

    List<MessageInteraction> findByUserIdAndInteractionType(Long userId, InteractionType interactionType);
}
