package com.example.my_chat_app;

// This class will represent the message structure coming from the client
// to an instance of this class.
public class ChatMessage {

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        PRIVATE,
        ROOM
    }

    private MessageType type;
    private String content;
    private String sender;
    private String recipient;
    private String roomId;

    // Default constructor is often needed for JSON deserialization
    public ChatMessage() {}

    public ChatMessage(MessageType type, String content, String sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
    }

    public ChatMessage(MessageType type, String content, String sender, String recipient, String roomId) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.recipient = recipient;
        this.roomId = roomId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "type=" + type +
               "content='" + content + '\'' +
               "sender='" + sender + '\'' +
               "recipient='" + recipient + '\'' +
               "roomId='" + roomId + '\'' +
               '}';
    }
}
