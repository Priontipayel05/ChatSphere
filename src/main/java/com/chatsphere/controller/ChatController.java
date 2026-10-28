package com.chatsphere.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import com.chatsphere.model.ChatMessage;
import com.chatsphere.repository.ChatMessageRepository;
import com.chatsphere.repository.ChatRoomRepository;
import com.chatsphere.repository.UserAccountRepository;
import com.chatsphere.service.ChatHistoryService;

@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatHistoryService chatHistoryService;
    private final ChatMessageRepository messageRepository;
    private final ChatRoomRepository roomRepository;
    private final UserAccountRepository userRepository;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatHistoryService chatHistoryService,
                          ChatMessageRepository messageRepository, ChatRoomRepository roomRepository, UserAccountRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatHistoryService = chatHistoryService;
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage message, Principal principal) {
        if (message == null || principal == null || message.getContent() == null || message.getRoom() == null) return;
        String sender = principal.getName().trim();
        String content = message.getContent().trim();
        String room = message.getRoom().trim();
        if (sender.isEmpty() || content.isEmpty() || content.length() > 4000
                || !room.matches("[A-Za-z0-9_-]{2,40}")
                || roomRepository.findByNameKey(room.toLowerCase(Locale.ROOT)).isEmpty()) return;

        message.setSender(sender);
        userRepository.findByUsernameKey(sender.toLowerCase(Locale.ROOT)).ifPresent(account -> {
            message.setSenderDisplayName(account.getDisplayName());
            message.setSenderAvatarColor(account.getAvatarColor());
        });
        message.setContent(content);
        message.setRoom(room);
        message.setType("CHAT");
        message.setTimestamp(System.currentTimeMillis());
        if (message.getReplyToId() != null) {
            ChatMessage parent = messageRepository.findById(message.getReplyToId()).orElse(null);
            if (parent != null && parent.getRoom().equals(room) && !parent.isDeleted()) {
                message.setReplyToSender(parent.getSender());
                String preview = parent.getContent();
                message.setReplyToContent(preview.length() > 180 ? preview.substring(0, 180) : preview);
            } else {
                message.setReplyToId(null);
                message.setReplyToSender(null);
                message.setReplyToContent(null);
            }
        }
        chatHistoryService.addMessage(message);
        messagingTemplate.convertAndSend("/topic/room/" + room, message);
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload Map<String, Object> payload, Principal principal) {
        if (principal == null || payload == null) return;
        Object roomValue = payload.get("room");
        Object typingValue = payload.get("typing");
        if (!(roomValue instanceof String room) || !room.matches("[A-Za-z0-9_-]{2,40}")
                || roomRepository.findByNameKey(room.toLowerCase(Locale.ROOT)).isEmpty()) return;
        messagingTemplate.convertAndSend("/topic/typing/" + room,
                (Object) Map.of("username", principal.getName(), "typing", Boolean.TRUE.equals(typingValue)));
    }

    @MessageMapping("/chat.presence")
    public void presence(Principal principal) {
        if (principal != null) messagingTemplate.convertAndSend("/topic/presence",
                (Object) Map.of("username", principal.getName(), "online", true));
    }

    @SubscribeMapping("/chat.history/{room}")
    public List<ChatMessage> getRoomHistory(@DestinationVariable("room") String room) {
        if (room == null || !room.matches("[A-Za-z0-9_-]{2,40}")
                || roomRepository.findByNameKey(room.toLowerCase(Locale.ROOT)).isEmpty()) return List.of();
        return chatHistoryService.getMessages(room);
    }
}
