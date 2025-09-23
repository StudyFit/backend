package com.farmers.studyfit.domain.chat.repository;

import com.farmers.studyfit.domain.chat.entity.ChatMessage;
import com.farmers.studyfit.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    Page<ChatMessage> findByChatRoomOrderByTimeDesc(ChatRoom chatRoom, Pageable pageable);
    
    List<ChatMessage> findByChatRoomOrderByTimeDesc(ChatRoom chatRoom);
    
    long countByChatRoomAndStatusFalse(ChatRoom chatRoom);
    
    List<ChatMessage> findByChatRoomAndStatusFalse(ChatRoom chatRoom);
}