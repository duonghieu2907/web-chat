package com.example.my_chat_app;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration // Marks this class as a Spring configuration class
@EnableWebSocketMessageBroker // Enables WebSocket message handling, backed by a message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Registers a STOMP endpoint at /ws.
        // The .withSockJS() enables SockJS fallback options for browsers that don't support WebSockets.
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Enables a simple in-memory message broker.
        // Messages whose destination starts with "/topic" will be routed to the message broker.
        // The broker then broadcasts these messages to all connected clients that are subscribed to that topic.
        config.enableSimpleBroker("/user", "/topic");

        // Sets the prefix for messages that are bound for methods annotated with @MessageMapping.
        // For example, a client sends a message to /app/sendMessage, which will be routed to a controller method.
        config.setApplicationDestinationPrefixes("/app");

        config.setUserDestinationPrefix("/user");
    }
}