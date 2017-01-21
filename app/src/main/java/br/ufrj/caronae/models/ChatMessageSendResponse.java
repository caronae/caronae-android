package br.ufrj.caronae.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Luis on 1/11/2017.
 */
public class ChatMessageSendResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private String messageId;

    public ChatMessageSendResponse(String message, String messageId) {
        this.message = message;
        this.messageId = messageId;
    }

    public ChatMessageSendResponse() {
    }

    public String getMessageId() {
        return messageId;
    }

    public String getResponseMessage() {
        return message;
    }
}
