package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class ChatMessageReceived extends SugarRecord<ChatMessageReceived> {
    private String senderName;
    private String senderId;
    private String message;
    private String rideId;

    public ChatMessageReceived(String senderName, String senderId, String message, String rideId) {
        this.senderName = senderName;
        this.senderId = senderId;
        this.message = message;
        this.rideId = rideId;
    }

    public ChatMessageReceived() {
    }

    public String getRideId() {
        return rideId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
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

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }
}
