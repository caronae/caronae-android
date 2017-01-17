package br.ufrj.caronae.models;

import java.util.List;

/**
 * Created by Luis on 1/12/2017.
 */
public class ModelReceivedFromChat {

    private List<ChatMessageReceivedFromJson> messages;

    public ModelReceivedFromChat(List<ChatMessageReceivedFromJson> messages){
        this.messages = messages;
    }

    public List<ChatMessageReceivedFromJson> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageReceivedFromJson> messages) {
        this.messages = messages;
    }
}
