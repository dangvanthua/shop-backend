package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.Chat;
import com.thuan.shop_backend.model.ChatConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(name = ChatConstants.FIND_CHAT_BY_SENDER_ID)
    List<Chat> findChatsBySenderId(@Param("senderId") long senderId, Pageable pageable);

    @Query(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER)
    Optional<Chat> findChatByReceiverAndSender(
            @Param("senderId") long senderId,
            @Param("recipientId") long recipientId);

    @Query("SELECT c FROM Chat c " +
            "WHERE c.sender.id IN :sellerIds " +
            "OR c.recipient.id IN :sellerIds " +
            "ORDER BY c.createdDate DESC")
    List<Chat> findChatsWithSellers(@Param("sellerIds") List<Long> sellerIds);
}
