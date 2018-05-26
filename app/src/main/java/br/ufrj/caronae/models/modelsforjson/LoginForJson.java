package br.ufrj.caronae.models.modelsforjson;

import com.google.gson.annotations.SerializedName;

public class LoginForJson extends TokenForJson {

    @SerializedName("id_ufrj")
    private String idUfrj;

    public LoginForJson(String token, String idUfrj) {
        super(token);
        this.idUfrj = idUfrj;
    }
}
