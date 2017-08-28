package br.ufrj.caronae.models.modelsforjson;

public class RideFiltersForJson {
    private String location;
    private String center;
    private String zone;
    private String campi;

    public String getResumeLocation() {
        return resumeLocation;
    }

    private String resumeLocation;

    public String getZone() {
        return zone;
    }

    public RideFiltersForJson(String location, String center, String campi, String zone, String resumeLocation) {
        this.location = location;
        this.center = center;
        this.campi = campi;
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

    public String getCampi() {
        return campi;
    }

    public void setCampi(String campi) {
        this.campi = campi;
    }
}
