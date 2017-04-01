package br.ufrj.caronae.models.modelsforjson;

public class RideFiltersForJson {
    private String location;
    private String center;

    public RideFiltersForJson(String location, String center) {
        this.location = location;
        this.center = center;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }
}
