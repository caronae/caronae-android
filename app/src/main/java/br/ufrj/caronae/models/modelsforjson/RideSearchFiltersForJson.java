package br.ufrj.caronae.models.modelsforjson;

public class RideSearchFiltersForJson {
    private String location;
    private String date;
    private String time;
    private String center;
    private boolean go;
    private String locationResumedField;

    public RideSearchFiltersForJson(String location, String date, String time, String center, boolean go, String locationResumedField) {
        this.location = location;
        this.date = date;
        this.time = time;
        this.center = center;
        this.go = go;
        this.locationResumedField = locationResumedField;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public boolean isGo() {
        return go;
    }

    public void setGo(boolean go) {
        this.go = go;
    }

    public String getLocationResumedField() {
        return locationResumedField;
    }

    public void setLocationResumedField(String locationResumedField) {
        this.locationResumedField = locationResumedField;
    }
}
