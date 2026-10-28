package com.chatsphere.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.chatsphere.model.ChatRoom;
import com.chatsphere.repository.ChatRoomRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private static final Pattern NAME = Pattern.compile("[A-Za-z0-9_-]{2,40}");
    private final ChatRoomRepository rooms;
    public RoomController(ChatRoomRepository rooms) { this.rooms = rooms; }

    @GetMapping public ResponseEntity<?> list(HttpSession session) {
        if (!loggedIn(session)) return unauthorized();
        if (rooms.count() == 0) {
            rooms.save(new ChatRoom("general", "general", "Community chat", "system", System.currentTimeMillis()));
            rooms.save(new ChatRoom("hangout", "hangout", "Casual conversations", "system", System.currentTimeMillis()));
            rooms.save(new ChatRoom("ideas", "ideas", "Share your thoughts", "system", System.currentTimeMillis()));
        }
        List<Map<String,Object>> result = rooms.findAllByOrderByNameAsc().stream().map(r -> Map.<String,Object>of("id",r.getId(),"name",r.getName(),"description",r.getDescription()==null?"Community room":r.getDescription(),"createdBy",r.getCreatedBy())).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping public ResponseEntity<?> create(@RequestBody RoomRequest request, HttpSession session) {
        if (!loggedIn(session)) return unauthorized();
        if (request == null || request.name == null || !NAME.matcher(request.name.trim()).matches())
            return ResponseEntity.badRequest().body(Map.of("message", "Room name must be 2–40 characters using letters, numbers, _ or -."));
        String name = request.name.trim();
        String key = name.toLowerCase(Locale.ROOT);
        if (rooms.existsByNameKey(key)) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "That room already exists."));
        String description = request.description == null ? "A new place to connect" : request.description.trim();
        if (description.length() > 120) return ResponseEntity.badRequest().body(Map.of("message", "Description must be 120 characters or fewer."));
        String creator = (String) session.getAttribute("authenticatedUser");
        ChatRoom room = rooms.save(new ChatRoom(name, key, description, creator, System.currentTimeMillis()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id",room.getId(),"name",room.getName(),"description",room.getDescription()==null?"":room.getDescription(),"createdBy",room.getCreatedBy()));
    }
    private boolean loggedIn(HttpSession s) { return s.getAttribute("authenticatedUser") instanceof String; }
    private ResponseEntity<Map<String,String>> unauthorized() { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Please log in first.")); }
    public static class RoomRequest { public String name; public String description; }
}
