package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class ChatMessageReceived extends SugarRecord<ChatMessageReceived> {
    private String rideId;
    private String senderName;
    private String message;

    public ChatMessageReceived() {
    }

    public ChatMessageReceived(String senderName, String message, String rideId) {
        this.senderName = senderName;
        this.message = message;
        this.rideId = rideId;
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
}
