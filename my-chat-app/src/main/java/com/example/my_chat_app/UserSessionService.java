package com.example.my_chat_app;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class UserSessionService {
    private final Set<String> connectedUsers = ConcurrentHashMap.newKeySet();

    public void addUser(String userId) {
        connectedUsers.add(userId);
    }

    public boolean exists(String userId) {
        return connectedUsers.contains(userId);
    }

    public Set<String> getAllUsers() {
        return connectedUsers;
    }
}
