package br.ufrj.caronae.models;

import com.google.gson.annotations.SerializedName;

public class UserChatMessageReceived {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public UserChatMessageReceived(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
