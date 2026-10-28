package com.chatsphere.config;

import java.security.Principal;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class PresenceEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    public PresenceEventListener(SimpMessagingTemplate messagingTemplate) { this.messagingTemplate = messagingTemplate; }

    @EventListener
    public void connected(SessionConnectedEvent event) {
        Principal user = event.getUser();
        if (user != null) messagingTemplate.convertAndSend("/topic/presence", (Object) Map.of("username", user.getName(), "online", true));
    }

    @EventListener
    public void disconnected(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null) messagingTemplate.convertAndSend("/topic/presence", (Object) Map.of("username", user.getName(), "online", false));
    }
}
