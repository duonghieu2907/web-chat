package com.example.my_chat_app;

public class ChatRoom {
    private String roomId;
    private String name;
    private String admin;

    public ChatRoom() {}

    public ChatRoom(String roomId, String name, String admin) {
        this.roomId = roomId;
        this.name = name;
        this.admin = admin;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "roomId=" + roomId +
               "name='" + name + '\'' +
               "admin='" + admin + '\'' +
               '}';
    }
}
