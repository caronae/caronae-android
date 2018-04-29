package br.ufrj.caronae.models;

import java.util.Collections;
import java.util.List;

public class Zone
{
    private String name;
    private String color;
    private List<String> neighborhoods;

    public String getName()
    {
        return name;
    }

    public String getColor()
    {
        return color;
    }

    public List<String> getNeighborhoods()
    {
        Collections.sort(neighborhoods);
        return neighborhoods;
    }
}
