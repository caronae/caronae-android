package br.ufrj.caronae.models;

import java.text.Collator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
        Collator collator = Collator.getInstance(new Locale("pt"));
	    Collections.sort(neighborhoods, collator);
        return neighborhoods;
    }
}
