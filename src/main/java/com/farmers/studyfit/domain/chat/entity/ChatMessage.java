package com.farmers.studyfit.domain.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CHAT")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_ID")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ROOM_ID", nullable = false)
    private ChatRoom chatRoom;
    
    @Column(name = "SENDER", nullable = false)
    private String sender; // TEACHER 또는 STUDENT (ERD에 맞춤)
    
    @Column(name = "TIME", nullable = false)
    private LocalDateTime time;
    
    @Column(name = "CONTENT", nullable = false)
    private String content; // 메시지 내용 (이미지인 경우 URL)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private MessageType type;
    
    @Column(name = "STATUS", nullable = false)
    @Builder.Default
    private boolean status = false;
    
    public enum MessageType {
        TEXT, IMAGE
    }
    
    @PrePersist
    protected void onCreate() {
        time = LocalDateTime.now();
    }
}
