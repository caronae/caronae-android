package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class Places extends SugarRecord
{
    private Zone[] zones;
    private Campi[] campi;
    private Institution[] institutions;

    public Places()
    {

    }

    public void setZones(Zone[] zones) {
        this.zones = zones;
    }

    public void setCampi(Campi[] campi) {
        this.campi = campi;
    }

    public void setInstitutions(Institution[] institutions) {
        this.institutions = institutions;
    }

    public Zone[] getZones() {
        return zones;
    }

    public Campi[] getCampi() {
        return campi;
    }

    public Institution[] getInstitutions() {
        return institutions;
    }
}
