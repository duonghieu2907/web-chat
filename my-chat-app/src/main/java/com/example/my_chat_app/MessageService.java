package com.example.my_chat_app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final Map<String, List<ChatMessage>> roomMessages = new ConcurrentHashMap<>();

    public void saveMessage(String roomId, ChatMessage message) {
        roomMessages.computeIfAbsent(roomId, k -> new ArrayList<>()).add(message);
    }

    public List<ChatMessage> getMessagesForRoom(String roomId) {
        return roomMessages.getOrDefault(roomId, new ArrayList<>());
    }
}
