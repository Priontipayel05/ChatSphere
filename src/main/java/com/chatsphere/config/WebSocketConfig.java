
package com.chatsphere.config;

import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOriginPatterns("http://localhost:8081");
    }

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration
    ) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(
                    Message<?> message,
                    MessageChannel channel
            ) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(
                                message,
                                StompHeaderAccessor.class
                        );

                if (accessor == null) {
                    return message;
                }

                StompCommand command = accessor.getCommand();

                if (command == StompCommand.CONNECT
                        || command == StompCommand.SEND
                        || command == StompCommand.SUBSCRIBE) {

                    Map<String, Object> attributes =
                            accessor.getSessionAttributes();

                    Object username = attributes == null
                            ? null
                            : attributes.get("authenticatedUser");

                    if (!(username instanceof String)
        || ((String) username).isBlank()) {
    throw new MessageDeliveryException(
            message,
            "Login required to use ChatSphere."
    );
}

// Associate the authenticated username with this WebSocket session.
if (command == StompCommand.CONNECT) {
    accessor.setUser(() -> (String) username);
}
                }

                return message;
            }
        });
    }
}
