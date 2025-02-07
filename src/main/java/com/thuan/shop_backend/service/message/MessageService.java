package com.thuan.shop_backend.service.message;

import com.thuan.shop_backend.component.AuthComponent;
import com.thuan.shop_backend.constant.MessageState;
import com.thuan.shop_backend.constant.NotificationType;
import com.thuan.shop_backend.dto.request.message.MessageRequest;
import com.thuan.shop_backend.dto.response.message.MessageResponse;
import com.thuan.shop_backend.dto.response.message.MessageResponses;
import com.thuan.shop_backend.dto.response.notification.NotificationResponse;
import com.thuan.shop_backend.entity.Chat;
import com.thuan.shop_backend.entity.Message;
import com.thuan.shop_backend.entity.User;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.ChatRepository;
import com.thuan.shop_backend.repository.MessageRepository;
import com.thuan.shop_backend.service.notification.INotificationService;
import com.thuan.shop_backend.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService{

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final INotificationService notificationService;
    private final AuthComponent authComponent;
    private final IUserService userService;

    @Override
    @Transactional
    public void saveMessage(MessageRequest messageRequest) {

        Chat chat = chatRepository.findById(messageRequest.getChatId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_NOT_EXISTED));

        Message message = Message.builder()
                .messageContent(messageRequest.getContent())
                .chat(chat)
                .senderId(messageRequest.getSenderId())
                .receiverId(messageRequest.getReceiverId())
                .type(messageRequest.getType())
                .state(MessageState.SENT)
                .build();

        message = messageRepository.save(message);

        // implement notify for user when chat
        NotificationResponse notificationResponse = NotificationResponse.builder()
                .chatId(chat.getId())
                .messageType(messageRequest.getType())
                .content(messageRequest.getContent())
                .senderId(messageRequest.getSenderId())
                .receiverId(messageRequest.getReceiverId())
                .type(NotificationType.MESSAGE)
                .chatName(chat.getTargetChatName(message.getSenderId()))
                .build();

        notificationService.sendNotification(message.getReceiverId(), notificationResponse);
    }

    @Override
    public MessageResponses findChatMessages(long chatId) {

        List<Message> messages = messageRepository.findMessagesByChatId(chatId);

        List<MessageResponse> messageResponses = messages
                .stream()
                .map(MessageResponse::fromMessage)
                .toList()
                .reversed();

        return MessageResponses.builder()
                .messageResponses(messageResponses)
                .build();
    }

    @Override
    @Transactional
    public void setMessagesToSeen(long chatId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_NOT_EXISTED));

        long recipientId = getRecipientId(chat);
        messageRepository.setMessagesToSeenByChatId(chatId, MessageState.SEEN);

        NotificationResponse notificationResponse = NotificationResponse.builder()
                .chatId(chat.getId())
                .type(NotificationType.SEEN)
                .receiverId(recipientId)
                .senderId(getSenderId(chat))
                .build();

        notificationService.sendNotification(recipientId, notificationResponse);
    }

    private long getSenderId(Chat chat) {
        String email = authComponent.getEmailFromAuthentication();
        User user = userService.getUserByEmail(email);
        if(Objects.equals(chat.getSender().getId(), user.getId())) {
            return chat.getSender().getId();
        }

        return chat.getRecipient().getId();
    }

    private long getRecipientId(Chat chat) {
        String email = authComponent.getEmailFromAuthentication();
        User user = userService.getUserByEmail(email);
        if(Objects.equals(chat.getSender().getId(), user.getId())) {
            return chat.getRecipient().getId();
        }

        return chat.getSender().getId();
    }
}
