package com.example.my_chat_app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, String>> findUser(@PathVariable String userId) {
        // System.out.println("User id requested: " + userId);
        // System.out.println("Available users: " + userSessionService.getAllUsers().stream().toList());
        if (userSessionService.exists(userId)) {
            Map<String, String> response = new HashMap<>();
            response.put("userId", userId);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userA}/{userB}/messages")
    public List<ChatMessage> getPrivateMessages(@PathVariable String userA, @PathVariable String userB) {
        System.err.println("Retreive messages between " + userA + " and " + userB);
        return messageService.getPrivateMessages(userA, userB);
    }
}
