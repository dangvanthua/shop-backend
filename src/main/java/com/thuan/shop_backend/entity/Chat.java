package com.thuan.shop_backend.entity;

import com.thuan.shop_backend.constant.MessageState;
import com.thuan.shop_backend.constant.MessageType;
import com.thuan.shop_backend.model.ChatConstants;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID,
        query = "SELECT DISTINCT c FROM Chat c WHERE c.sender.id = :senderId " +
                "OR c.recipient.id = :senderId ORDER BY c.createdDate DESC"
)
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER,
        query = "SELECT DISTINCT c FROM Chat c WHERE (c.sender.id = :senderId AND c.recipient.id = :recipientId) " +
                "OR (c.sender.id = :recipientId AND c.recipient.id = :senderId) ORDER BY c.createdDate DESC"
)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date", nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("sentAt DESC")
    private List<Message> messages;

    @Transient
    public String getChatAvatar(long senderId) {
        return sender.getId() == senderId ? recipient.getAvatar() : sender.getAvatar();
    }

    @Transient
    public String getChatName(long senderId) {
        return recipient.getId() == senderId ? sender.getFullName() : recipient.getFullName();
    }

    @Transient
    public String getTargetChatName(long senderId) {
        return sender.getId() == senderId ? recipient.getFullName() : sender.getFullName();
    }

    @Transient
    public long getUnreadMessages(long senderId) {
        return this.messages.stream()
                .filter(m -> m.getReceiverId() == senderId && m.getState() == MessageState.SENT)
                .count();
    }

    @Transient
    public String getLastMessage() {
        return (messages != null && !messages.isEmpty())
                ? (messages.get(0).getType() != MessageType.TEXT ? "Attachment" : messages.get(0).getMessageContent())
                : null;
    }

    @Transient
    public LocalDateTime getLastMessageTime() {
        return (messages != null && !messages.isEmpty()) ? messages.get(0).getSentAt() : null;
    }
}
