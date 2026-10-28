package com.chatsphere.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chatsphere.model.ChatMessage;
import com.chatsphere.repository.ChatMessageRepository;

@Service
public class ChatHistoryService {
    private final ChatMessageRepository messages;

    public ChatHistoryService(ChatMessageRepository messages) {
        this.messages = messages;
    }

    @Transactional
    public void addMessage(ChatMessage message) {
        messages.save(message);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(String room) {
        if (room == null || room.isBlank()) return Collections.emptyList();
        List<ChatMessage> latestFirst = messages.findTop100ByRoomOrderByTimestampDesc(room);
        List<ChatMessage> chronological = new ArrayList<>(latestFirst);
        Collections.reverse(chronological);
        return chronological;
    }
}
