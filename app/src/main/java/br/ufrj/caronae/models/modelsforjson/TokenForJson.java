package br.ufrj.caronae.models.modelsforjson;

import com.google.gson.annotations.SerializedName;

public class TokenForJson {

    @SerializedName("token")
    private String token;

    public TokenForJson(String token) {
        this.token = token;
    }
}
