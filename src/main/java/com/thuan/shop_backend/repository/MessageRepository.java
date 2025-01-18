package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.constant.MessageState;
import com.thuan.shop_backend.entity.Message;
import com.thuan.shop_backend.model.MessageConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID)
    List<Message> findMessagesByChatId(@Param("chatId") long chatId, Pageable pageable);

    @Query(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT)
    @Modifying
    void setMessagesToSeenByChatId(
            @Param("chatId") long chatId,
            @Param("newState") MessageState state);
}
