package br.ufrj.caronae.models;

import java.util.List;

public class ModelReceivedFromChat {

    private List<ChatMessageReceivedFromJson> messages;

    public ModelReceivedFromChat(List<ChatMessageReceivedFromJson> messages){
        this.messages = messages;
    }

    public List<ChatMessageReceivedFromJson> getMessages() {
        return messages;
    }

}
