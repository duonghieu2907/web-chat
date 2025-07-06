package com.example.my_chat_app;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RoomService {
    private final Map<String, ChatRoom> rooms = new ConcurrentHashMap<>();

    public void addRoom(ChatRoom room) {
        rooms.put(room.getRoomId(), room);
    }

    public ChatRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Collection<ChatRoom> getAllRooms() {
        return rooms.values();
    }

    public boolean exists(String roomId) {
        return rooms.containsKey(roomId);
    }
}
