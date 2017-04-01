package br.ufrj.caronae.models.modelsforjson;

public class RideFiltersForJson {
    private String location;
    private String center;
    private String zone;

    public String getResumeLocation() {
        return resumeLocation;
    }

    private String resumeLocation;

    public String getZone() {
        return zone;
    }

    public RideFiltersForJson(String location, String center, String zone, String resumeLocation) {
        this.location = location;
        this.center = center;
        this.zone = zone;
        this.resumeLocation = resumeLocation;
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
