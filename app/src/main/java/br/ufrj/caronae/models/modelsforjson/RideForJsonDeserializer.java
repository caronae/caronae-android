package br.ufrj.caronae.models.modelsforjson;

import java.util.List;

public class RideForJsonDeserializer {

    public List<RideForJson> getData() {
        return data;
    }

    private List<RideForJson> data;

    private List<RideForJson> rides;

    public List<RideForJson> getRides(){
        return rides;
    }
}
