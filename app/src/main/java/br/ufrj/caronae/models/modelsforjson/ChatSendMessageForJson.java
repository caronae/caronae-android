package br.ufrj.caronae.models.modelsforjson;

/**
 * Created by Luis on 1/12/2017.
 */
public class ChatSendMessageForJson {

    private String message;

    public ChatSendMessageForJson(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
