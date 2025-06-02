package com.example.my_chat_app;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // This annotation marks the class as a Spring MVC Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

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
     * Handles the initial "add user" message when a client connects and sends their username.
     * This method is primarily used to set the user's principal on the WebSocket session.
     * The @Payload ChatMessage will still be processed, but its content might be empty or a welcome message.
     */
    @MessageMapping("/addUser") // Client will send to /app/addUser
    @SendTo("/topic/public") // Broadcasts a welcome message to all subscribers
    public String addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Log all native headers received
        logger.info("Received CONNECT message. Native header: {}", headerAccessor.getMessageHeaders().get("nativeHeaders"));
        
        // Retrieve the username from the STOMP CONNECT header (sent by the client)
        String username = headerAccessor.getFirstNativeHeader("username");
        
        // Log the username as retrieved
        logger.info("Attempted to retrieve username from header: {}", username);

        if (username == null || username.trim().isEmpty()) {
            // Fallback if username is missing or empty
            String sessionId = headerAccessor.getSessionId();
            if (sessionId != null && sessionId.length() >= 8) {
                username = "AnonymousUser_" + sessionId.substring(0, 8);
            } else if (sessionId != null) {
                username = "AnonymousUser_Unknown";
            }
        }

        // Set the user principal on the WebSocket session.
        // This makes the username available in subsequent messages from this session.
        headerAccessor.setUser(new UsernamePrincipal(username));

        return "[" + username + "] has joined the chat!"; 

    }


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
        // Get the username from the session's Principal that we set in addUser method
        String username = "Anonymous";
        Principal user = headerAccessor.getUser();
        if (user != null) {
            username = user.getName();
        }
        return "User [" + username + "]: " + chatMessage.getContent(); // Truncate ID for readability
    }
}
