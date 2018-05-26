package br.ufrj.caronae.models;

import com.google.gson.annotations.SerializedName;

public class ChatMessageSendResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private String messageId;

    public ChatMessageSendResponse(String message, String messageId) {
        this.message = message;
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }
}
