package com.chatsphere.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.chatsphere.model.ChatMessage;
import com.chatsphere.repository.ChatMessageRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final ChatMessageRepository messages;
    private final SimpMessagingTemplate messagingTemplate;
    public MessageController(ChatMessageRepository messages, SimpMessagingTemplate messagingTemplate) { this.messages = messages; this.messagingTemplate = messagingTemplate; }

    @GetMapping("/search") public ResponseEntity<?> search(@RequestParam String room, @RequestParam String q, HttpSession session) {
        if (!loggedIn(session)) return unauthorized();
        String cleanRoom = room == null ? "" : room.trim();
        String query = q == null ? "" : q.trim();
        if (cleanRoom.isEmpty() || query.length() < 2 || query.length() > 100) return ResponseEntity.badRequest().body(Map.of("message", "Enter at least 2 characters to search."));
        List<ChatMessage> found = messages.findTop100ByRoomAndContentContainingIgnoreCaseOrderByTimestampDesc(cleanRoom, query);
        return ResponseEntity.ok(found);
    }

    @PutMapping("/{id}") public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody EditRequest request, HttpSession session) {
        String user = username(session); if (user == null) return unauthorized();
        ChatMessage message = messages.findById(id).orElse(null);
        if (message == null) return ResponseEntity.notFound().build();
        if (!user.equals(message.getSender())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You can only edit your own messages."));
        if (message.isDeleted()) return ResponseEntity.badRequest().body(Map.of("message", "Deleted messages cannot be edited."));
        if (request == null || request.content == null || request.content.trim().isEmpty() || request.content.trim().length() > 4000) return ResponseEntity.badRequest().body(Map.of("message", "Message must contain 1–4000 characters."));
        message.setContent(request.content.trim()); message.setEdited(true); messages.save(message);
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoom(), message);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{id}") public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        String user = username(session); if (user == null) return unauthorized();
        ChatMessage message = messages.findById(id).orElse(null);
        if (message == null) return ResponseEntity.notFound().build();
        if (!user.equals(message.getSender())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You can only delete your own messages."));
        message.setContent("This message was deleted."); message.setDeleted(true); messages.save(message);
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoom(), message);
        return ResponseEntity.ok(message);
    }
    private String username(HttpSession s) { Object u=s.getAttribute("authenticatedUser"); return u instanceof String?(String)u:null; }
    private boolean loggedIn(HttpSession s) { return username(s)!=null; }
    private ResponseEntity<Map<String,String>> unauthorized() { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Please log in first.")); }
    public static class EditRequest { public String content; }
}
