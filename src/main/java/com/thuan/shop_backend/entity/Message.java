package com.thuan.shop_backend.entity;

import com.thuan.shop_backend.constant.MessageState;
import com.thuan.shop_backend.constant.MessageType;
import com.thuan.shop_backend.model.MessageConstants;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedQuery(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID,
        query = "SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sentAt"
)
@NamedQuery(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT,
        query = "UPDATE Message m SET m.state = :newState WHERE m.chat.id = :chatId"
)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_content", nullable = false)
    private String messageContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageState state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(name = "sent_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "media_file_path")
    private String mediaFilePath;
}
