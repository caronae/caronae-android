package br.ufrj.caronae.models.modelsforjson;

public class RideFiltersForJson {
    private String location;
    private String center;
    private String zone;
    private String campus;

    public String getResumeLocation() {
        return resumeLocation;
    }

    private String resumeLocation;

    public String getZone() {
        return zone;
    }

    public RideFiltersForJson(String location, String center, String campus, String zone, String resumeLocation) {
        this.location = location;
        this.center = center;
        this.campus = campus;
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

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }
}
