package br.ufrj.caronae.models;

/**
 * Created by Luis on 5/9/2017.
 */

public class RepeatisUntil {

    String date;
    String timezone_type;
    String timezone;

    public RepeatisUntil() {
    }

    public RepeatisUntil(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
