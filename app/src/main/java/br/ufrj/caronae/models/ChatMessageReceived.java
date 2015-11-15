package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class ChatMessageReceived extends SugarRecord<ChatMessageReceived> {
    private String senderName;
    private String message;

    public ChatMessageReceived() {
    }

    public ChatMessageReceived(String senderName, String message) {
        this.senderName = senderName;
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }
}
