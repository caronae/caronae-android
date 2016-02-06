package br.ufrj.caronae.models.modelsforjson;

import java.util.HashMap;
import java.util.Map;

import br.ufrj.caronae.App;

public class ChatMessageSent {
    private String to, priority;
    private boolean content_available;
    private Map<String, String> data;

    public ChatMessageSent(String dbId, String message, String time) {
        to = "/topics/" + dbId;
        priority = "high";
        data = new HashMap<>();
        data.put("message", message);
        data.put("rideId", dbId);
        data.put("msgType", "chat");
        data.put("senderName", App.getUser().getName());
        data.put("senderId", App.getUser().getDbId()+"");
        data.put("time", time);

        content_available = true;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public boolean isContent_available() {
        return content_available;
    }

    public void setContent_available(boolean content_available) {
        this.content_available = content_available;
    }
}
