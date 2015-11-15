package br.ufrj.caronae.models.modelsforjson;

import java.util.HashMap;
import java.util.Map;

import br.ufrj.caronae.App;

public class ChatMessageToSend {
    private String to;
    private Map<String, String> data;

    public ChatMessageToSend(int dbId, String message) {
        to = "/topics/" + dbId;
        data = new HashMap<>();
        data.put("message", message);
        data.put("msgType", "chat");
        data.put("senderName", App.getUser().getName());
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
