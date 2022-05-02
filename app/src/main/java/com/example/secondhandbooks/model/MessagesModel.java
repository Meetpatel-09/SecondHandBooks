package com.example.secondhandbooks.model;

public class MessagesModel {

    String message;
    String senderId;
    String receiverId;
    long timeStamp;
    String messageTime;

    public MessagesModel() {
    }

    public MessagesModel(String message, String senderId, String receiverId, long timeStamp, String messageTime) {
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timeStamp = timeStamp;
        this.messageTime = messageTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
}
