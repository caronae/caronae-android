package br.ufrj.caronae.models.modelsforjson;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MyRidesForJson {

    @SerializedName("pending_rides")
    private List<RideForJson> pendingRides;
    @SerializedName("active_rides")
    private List<RideForJson> activeRides;
    @SerializedName("offered_rides")
    private List<RideForJson> offeredRides;

    public List<RideForJson> getPendingRides()
    {
        return pendingRides;
    }

    public List<RideForJson> getActiveRides()
    {
        return activeRides;
    }

    public List<RideForJson> getOfferedRides()
    {
        return offeredRides;
    }
}
