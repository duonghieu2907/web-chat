package com.example.my_chat_app;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller // This annotation marks the class as a Spring MVC Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    public static final String USERNAME_SESSION_ATTRIBUTE = "username";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
        
        //logger.info("SessionConnectEvent - Attempted to retrieve username from CONNECT header: {}", username);

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
            
            // An alternative of /addUser

            // ChatMessage joinMessage = new ChatMessage(ChatMessage.MessageType.JOIN, username + " has joined the chat!", username);
            // messagingTemplate.convertAndSend("/topic/public", joinMessage);
        } else {
            logger.error("Session attributes are null for session {}", headerAccessor.getSessionId());
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = "Unknown User (Disconnected)";

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.containsKey(USERNAME_SESSION_ATTRIBUTE)) {
            username = (String) sessionAttributes.get(USERNAME_SESSION_ATTRIBUTE);
        }

        logger.info("SessionDisconnectEvent - User disconnected: {}", username);

        ChatMessage leaveMessage = new ChatMessage(ChatMessage.MessageType.LEAVE, username + " has left the chat.", username);
        messagingTemplate.convertAndSend("topic/public", leaveMessage);
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
     * Handles the initial "add user" message.
     * New sends a structured ChatMessage with type JOIN
     */
    @MessageMapping("/addUser")
    @SendTo("/topic/public") // Broadcasts a welcome message to all subscribers
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String username = getUsernameFromSession(headerAccessor);

        logger.info("User {} sent join message to /addUser.", username);

        return new ChatMessage(ChatMessage.MessageType.JOIN, username + " has joined the chat!", username); 

    }


    /**
     * Handles incoming chat messages from clients.
     *
     * @param chatMessage The message payload as a ChatMessage object.
     * @param headerAccessor Used to access STOMP headers.
     */
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        String senderUsername = getUsernameFromSession(headerAccessor);
        chatMessage.setSender(senderUsername);

        logger.info("Recieved message type: {} from sender {}", chatMessage.getType(), senderUsername);
        logger.info("Message content: {}", chatMessage.getContent());
        logger.info("Recipient: {}", chatMessage.getRecipient());

        if (chatMessage.getType() == ChatMessage.MessageType.PRIVATE) {
            String recipientUsername = chatMessage.getRecipient();

            if (recipientUsername != null && !recipientUsername.trim().isEmpty()) {
                messagingTemplate.convertAndSendToUser(
                    recipientUsername, 
                    "/queue/messages", 
                    chatMessage
                );

                logger.info("Private message from {} to {}: {}", senderUsername, recipientUsername, chatMessage.getContent());

                messagingTemplate.convertAndSendToUser(
                    senderUsername, 
                    "/queue/messages", 
                    new ChatMessage(ChatMessage.MessageType.CHAT, "(to " + recipientUsername + ")" + chatMessage.getContent(), senderUsername)
                );
            } else {
                logger.warn("Private message received without a recipient from user: {}", senderUsername);

                messagingTemplate.convertAndSendToUser(
                    senderUsername, 
                    "/queue/messages", 
                    new ChatMessage(ChatMessage.MessageType.CHAT, "Error: Private message requires a recipient", "System")
                );
            }
        } else {
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
            logger.info("Public message from {}: {}", senderUsername, chatMessage.getContent());
        }
    }
}
