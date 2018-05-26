package br.ufrj.caronae.models;

import com.google.gson.annotations.SerializedName;

public class ChatMessageReceivedFromJson {

    @SerializedName("body")
    private String message;

    @SerializedName("id")
    private String messageId;

    @SerializedName("date")
    private String time;

    @SerializedName("user")
    private UserChatMessageReceived user;

    public ChatMessageReceivedFromJson(UserChatMessageReceived user, String message, String rideId, String time){
        this.message = message;
        this.messageId = rideId;
        this.time = time;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTime() {
        return time;
    }

    public UserChatMessageReceived getUser() {
        return user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUser(UserChatMessageReceived user) {
        this.user = user;
    }
}
