package com.example.chatbot;

public class ChatMessage {
    private String message;
    private boolean isSent;

    public ChatMessage(String message, boolean isSent) {
        this.message = message;
        this.isSent = isSent;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setMessage(String newMessage) {
        this.message = newMessage;
    }
}
