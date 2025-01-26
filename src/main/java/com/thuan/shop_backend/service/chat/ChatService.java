package com.thuan.shop_backend.service.chat;

import com.thuan.shop_backend.component.AuthComponent;
import com.thuan.shop_backend.dto.response.chat.ChatResponse;
import com.thuan.shop_backend.entity.Chat;
import com.thuan.shop_backend.entity.User;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.ChatRepository;
import com.thuan.shop_backend.repository.OrderRepository;
import com.thuan.shop_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService implements IChatService{

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AuthComponent authComponent;

    @Override
    @Transactional(readOnly = true)
    public List<ChatResponse> getChatsByReceiverId(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        String email = authComponent.getEmailFromAuthentication();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Chat> chats = chatRepository.findChatsBySenderId(user.getId(), pageable);

        if(chats.isEmpty()) {
            List<Long> sellerIds = orderRepository.findSellerIdsByUserId(user.getId());

            if(!sellerIds.isEmpty()) {
                chats = chatRepository.findChatsWithSellers(sellerIds);
            }
        }

        return chats.stream()
                .map(chat -> ChatResponse.fromChat(chat, user.getId()))
                .toList();
    }

    @Override
    @Transactional
    public long createChat(long receiverId) {

        String emailUser = authComponent.getEmailFromAuthentication();
        User sender = userRepository.findByEmail(emailUser)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Optional<Chat> existingChat = chatRepository.findChatByReceiverAndSender(
                sender.getId(), receiverId);
        if(existingChat.isPresent()) {
            return existingChat.get().getId();
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Chat chat = Chat.builder()
                .sender(sender)
                .recipient(receiver)
                .build();

        Chat savedChat = chatRepository.save(chat);

        return savedChat.getId();
    }
}
