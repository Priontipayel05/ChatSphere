package com.chatsphere.controller;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.chatsphere.model.UserAccount;
import com.chatsphere.repository.UserAccountRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private static final Set<String> COLORS = Set.of("#7764ee", "#0f9f86", "#e87942", "#d9468f", "#3b82f6", "#64748b");
    private final UserAccountRepository users;
    public ProfileController(UserAccountRepository users) { this.users = users; }

    @GetMapping
    public ResponseEntity<?> get(HttpSession session) {
        UserAccount user = current(session);
        if (user == null) return unauthorized();
        return ResponseEntity.ok(view(user));
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody ProfileRequest request, HttpSession session) {
        UserAccount user = current(session);
        if (user == null) return unauthorized();
        if (request == null || request.displayName == null || request.displayName.trim().isEmpty() || request.displayName.trim().length() > 40)
            return ResponseEntity.badRequest().body(Map.of("message", "Display name must contain 1–40 characters."));
        if (request.statusMessage != null && request.statusMessage.trim().length() > 120)
            return ResponseEntity.badRequest().body(Map.of("message", "Status must be 120 characters or fewer."));
        String color = request.avatarColor == null ? user.getAvatarColor() : request.avatarColor;
        if (!COLORS.contains(color)) return ResponseEntity.badRequest().body(Map.of("message", "Choose a supported avatar color."));
        user.setDisplayName(request.displayName.trim());
        user.setStatusMessage(request.statusMessage == null ? "Ready to connect" : request.statusMessage.trim());
        user.setAvatarColor(color);
        users.save(user);
        return ResponseEntity.ok(view(user));
    }

    private UserAccount current(HttpSession session) {
        Object name = session.getAttribute("authenticatedUser");
        if (!(name instanceof String)) return null;
        return users.findByUsernameKey(((String) name).toLowerCase(java.util.Locale.ROOT)).orElse(null);
    }
    private Map<String,Object> view(UserAccount u) { return Map.of("username",u.getUsername(),"displayName",u.getDisplayName(),"statusMessage",u.getStatusMessage(),"avatarColor",u.getAvatarColor()); }
    private ResponseEntity<Map<String,String>> unauthorized() { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Please log in first.")); }
    public static class ProfileRequest { public String displayName; public String statusMessage; public String avatarColor; }
}
