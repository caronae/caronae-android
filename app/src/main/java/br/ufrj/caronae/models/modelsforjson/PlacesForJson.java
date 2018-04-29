package br.ufrj.caronae.models.modelsforjson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.ufrj.caronae.models.Campi;
import br.ufrj.caronae.models.Institution;
import br.ufrj.caronae.models.Zone;

public class PlacesForJson
{
    @SerializedName("zones")
    private List<Zone> zones;
    @SerializedName("campi")
    private List<Campi> campi;
    @SerializedName("institution")
    private Institution institutions;

    public List<Zone> getZones() {
        return zones;
    }

    public List<Campi> getCampi() {
        return campi;
    }

    public Institution getInstitutions() {
        return institutions;
    }
}
