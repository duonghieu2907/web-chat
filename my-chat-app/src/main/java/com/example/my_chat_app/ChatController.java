package com.example.my_chat_app;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // This annotation marks the class as a Spring MVC Controller
public class ChatController {

    @GetMapping("/chat") // This maps HTTP GET requests to the /chat URL to this method
    public String chatPage() {
        // Spring Boot, with Thymeleaf, will look for a template named "chat.html"
        // in src/main/resources/templates/
        return "chat";
    }

    @GetMapping("/")
    public String redirectToChat() {
        return "redirect:/chat";
    }

    // --- WebSocket Message Handling ---

    /**
     * Handles incoming chat messages from clients.
     *
     * @param chatMessage The message payload as a ChatMessage object.
     * @param headerAccessor Used to access STOMP headers, specifically the session ID for a unique sender identifier.
     * @return The formatted message to be broadcast to all subscribers.
     */
    @MessageMapping("/sendMessage") // Maps messages from "/app/sendMessage"
    @SendTo("/topic/public")      // Sends the return value of this method to "/topic/public"
    public String sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String userIdentifier = "Unknown";
        if (sessionId != null && sessionId.length() >= 8) {
            userIdentifier = sessionId.substring(0, 8);
        } else if (sessionId != null) {
            userIdentifier = sessionId;
        }
        return "User [" + userIdentifier + "]: " + chatMessage.getContent(); // Truncate ID for readability
    }
}
