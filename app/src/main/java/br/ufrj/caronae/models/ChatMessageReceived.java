package br.ufrj.caronae.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class ChatMessageReceived extends SugarRecord {

    @SerializedName("name")
    private String senderName;

    @SerializedName("id")
    private String senderId;

    @SerializedName("body")
    private String message;

    @SerializedName("id")
    private String rideId;

    @SerializedName("date")
    private String time;

    public ChatMessageReceived(String senderName, String senderId, String message, String rideId, String time) {
        this.senderName = senderName;
        this.senderId = senderId;
        this.message = message;
        this.rideId = rideId;
        this.time = time;
    }

    public ChatMessageReceived() {
    }

    public String getRideId() {
        return rideId;
    }

    public String getSenderName() {
        return senderName;
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

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
