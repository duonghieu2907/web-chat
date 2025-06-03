package com.example.my_chat_app;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller // This annotation marks the class as a Spring MVC Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    public static final String USERNAME_SESSION_ATTRIBUTE = "username";

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
     * This method listens for the initial STOMP CONNECT event.
     * It's the ideal place to retrieve and set the username from the CONNECT headers.
     * @param event The SessionConnectEvent containing the STOMP headers.
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Log all native headers received during the CONNECT event
        String username = headerAccessor.getFirstNativeHeader("username");
        logger.info("SessionConnectEvent - Attempted to retrieve username from CONNECT header: {}", username);

        

        if (username == null || username.trim().isEmpty()) {
            String sessionId = headerAccessor.getSessionId();

            if (sessionId != null && sessionId.length() >= 8) {
                username = "AnonymousUser_" + sessionId.substring(0, 8);
            } else {
                username = "Anonymous_Unknown";
            }

            logger.warn("SessionConnectEvent - Username header was null or empty, falling back to: {}", username);
        }

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            sessionAttributes.put(USERNAME_SESSION_ATTRIBUTE, username);
            logger.info("SessionConnectEvent - Username '{}' stored in session attributes for session {}", username, headerAccessor.getSessionId());
        } else {
            logger.error("Session attributes are null for session {}", headerAccessor.getSessionId());
        }

        // headerAccessor.setUser(new UsernamePrincipal(username));
        // logger.info("SessionConnectEvent - Principal set for session {}: {}", headerAccessor.getSessionId(), headerAccessor.getUser());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = "Unknown User (Disconnected)";
        // Principal user = headerAccessor.getUser();
        // if (user != null) {
        //     username = user.getName();
        // }

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.containsKey(USERNAME_SESSION_ATTRIBUTE)) {
            username = (String) sessionAttributes.get(USERNAME_SESSION_ATTRIBUTE);
        }

        logger.info("SessionDisconnectEvent - User disconnected: {}", username);
    }

    /**
     * Helper method to retrieve username from session attributes.
     */
    private String getUsernameFromSession(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.containsKey(USERNAME_SESSION_ATTRIBUTE)) {
            return (String) sessionAttributes.get(USERNAME_SESSION_ATTRIBUTE);
        }
        
        String sessionId = headerAccessor.getSessionId();
        if (sessionId != null && sessionId.length() >= 8) {
            return "AnonymousUser_" + sessionId.substring(0, 8);
        }
        return "Anonymous_UnknownSession";
    }

    /**
     * Handles the initial "add user" message when a client connects and sends their username.
     * This method is primarily used to set the user's principal on the WebSocket session.
     * The @Payload ChatMessage will still be processed, but its content might be empty or a welcome message.
     */
    @MessageMapping("/addUser") // Client will send to /app/addUser
    @SendTo("/topic/public") // Broadcasts a welcome message to all subscribers
    public String addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // // Log all native headers received
        // logger.info("Received CONNECT message. Native header: {}", headerAccessor.getMessageHeaders().get("nativeHeaders"));
        
        // // Retrieve the username from the STOMP CONNECT header (sent by the client)
        // String username = headerAccessor.getFirstNativeHeader("username");
        
        // // Log the username as retrieved
        // logger.info("Attempted to retrieve username from header: {}", username);

        // if (username == null || username.trim().isEmpty()) {
        //     // Fallback if username is missing or empty
        //     String sessionId = headerAccessor.getSessionId();
        //     if (sessionId != null && sessionId.length() >= 8) {
        //         username = "AnonymousUser_" + sessionId.substring(0, 8);
        //     } else if (sessionId != null) {
        //         username = "AnonymousUser_Unknown";
        //     }
        // }

        // // Set the user principal on the WebSocket session.
        // // This makes the username available in subsequent messages from this session.
        // headerAccessor.setUser(new UsernamePrincipal(username));

        // String username = "Anonymous";
        // Principal user = headerAccessor.getUser();
        // if (user != null) {
        //     username = user.getName();
        // }
        String username = getUsernameFromSession(headerAccessor);

        logger.info("User {} sent join message to /addUser. Principal in addUser: {}", username);

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
        // // Get the username from the session's Principal that we set in addUser method
        // String username = "Anonymous";
        // Principal user = headerAccessor.getUser();
        // // *** ADDED LOGGING FOR DEBUGGING ***
        // logger.info("sendMessage method called. Session ID: {}", headerAccessor.getSessionId());
        // logger.info("sendMessage method called. Principal in headerAccessor: {}", user);
        // if (user != null) {
        //     username = user.getName();
        // } else {
        //     logger.warn("Principal is null in sendMessage method for session: {}", headerAccessor.getSessionId());
        //     String sessionId = headerAccessor.getSessionId();
        //     if (sessionId != null && sessionId.length() >= 8) {
        //         username = "AnonymousUser_" + sessionId.substring(0, 8);
        //     } else if (sessionId != null) {
        //         username = "AnonymousUser_Unknown";
        //     }
        // }
        String username = getUsernameFromSession(headerAccessor);

        logger.info("sendMessage method called. Session ID: {}", headerAccessor.getSessionId());
        logger.info("sendMessage method called. Principal in headerAccessor: {}", headerAccessor.getUser());
        logger.info("sendMessage method called. Username from session attributes: {}", username);

        logger.info("User {}: {}", username, chatMessage.getContent());
        return "User [" + username + "]: " + chatMessage.getContent(); 
    }
}
