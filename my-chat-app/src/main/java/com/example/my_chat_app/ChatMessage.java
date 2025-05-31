package com.example.my_chat_app;

// This class will represent the message structure coming from the client
// Spring will automatically map the incoming JSON (e.g., {"content": "Hello"})
// to an instance of this class.
public class ChatMessage {

    private String content;

    // Default constructor is often needed for JSON deserialization
    public ChatMessage() {
    }

    public ChatMessage(String content) {
        this.content = content;
    }

    // Getter for the content field
    public String getContent() {
        return content;
    }

    // Setter for the content field
    public void setContent(String content) {
        this.content = content;
    }

    // Optional: toString() for easier debugging
    @Override
    public String toString() {
        return "ChatMessage{" +
               "content='" + content + '\'' +
               '}';
    }
}
