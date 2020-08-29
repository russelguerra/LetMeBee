package com.guerra.russel.letmebee.Collection;

public class Chats {

    String sender;
    String receiver;
    String message;
    long currentTime;

    public Chats() {
    }

    public Chats(String sender, String receiver, String message, long currentTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.currentTime = currentTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
