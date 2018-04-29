package br.ufrj.caronae.models;

import java.util.Collections;
import java.util.List;

public class Campi
{
    private String name;
    private String color;
    private List<String> centers;
    private List<String> hubs;

    public String getName()
    {
        return name;
    }

    public String getColor()
    {
        return color;
    }

    public List<String> getCenters() {
        Collections.sort(centers);
        return centers;
    }

    public List<String> getHubs() {
        Collections.sort(hubs);
        return hubs;
    }
}
