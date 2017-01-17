package br.ufrj.caronae.models;

/**
 * Created by Luis on 1/11/2017.
 */
public class ChatMessageSendResponse {

    private String message;
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
