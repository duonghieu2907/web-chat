package com.example.my_chat_app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final Map<String, List<ChatMessage>> messages = new ConcurrentHashMap<>();

    public void saveMessage(String chatKey, ChatMessage message) {
        messages.computeIfAbsent(chatKey, k -> new ArrayList<>()).add(message);
    }

    public void savePrivateMessage(String userA, String userB, ChatMessage message) {
        String chatKey = buildPrivateChatKey(userA, userB);
        messages.computeIfAbsent(chatKey, k -> new ArrayList<>()).add(message);
    }

    public List<ChatMessage> getMessagesForRoom(String chatKey) {
        return messages.getOrDefault(chatKey, new ArrayList<>());
    }

    public List<ChatMessage> getPrivateMessages(String userA, String userB) {
        String chatKey = buildPrivateChatKey(userA, userB);
        return messages.getOrDefault(chatKey, new ArrayList<>());
    }

    private static String buildPrivateChatKey(String userA, String userB) {
        return userA.compareTo(userB) < 0 ? userA + ":" + userB : userB + ":" + userA;
    }
}
