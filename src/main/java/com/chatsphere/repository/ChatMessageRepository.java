package com.chatsphere.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.chatsphere.model.ChatMessage;
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop100ByRoomOrderByTimestampDesc(String room);
    List<ChatMessage> findTop100ByRoomAndContentContainingIgnoreCaseOrderByTimestampDesc(String room, String query);
}
