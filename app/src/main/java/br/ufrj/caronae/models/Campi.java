package br.ufrj.caronae.models;

import java.text.Collator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
        Collator collator = Collator.getInstance(new Locale("pt"));
        Collections.sort(centers, collator);
        return centers;
    }

    public List<String> getHubs() {
        Collator collator = Collator.getInstance(new Locale("pt"));
        Collections.sort(hubs, collator);
        return hubs;
    }
}
