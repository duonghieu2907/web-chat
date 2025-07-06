package com.example.my_chat_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomRestController {
    
    @Autowired
    private RoomService roomService;

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoom> getRoom(@PathVariable String roomId) {
        System.out.println("Room id requested: " + roomId);
        System.out.println("Available rooms: " + roomService.getAllRooms().stream().map(ChatRoom::getRoomId).toList());
        ChatRoom room = roomService.getRoom(roomId);
        if (room != null) {
            System.out.println("Room id returned: " + room.getRoomId());
            return ResponseEntity.ok(room);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
