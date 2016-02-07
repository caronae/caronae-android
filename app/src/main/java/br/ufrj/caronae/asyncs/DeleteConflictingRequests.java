package br.ufrj.caronae.asyncs;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.RideIdForJson;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DeleteConflictingRequests extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... arg0) {
        String rideId = arg0[0];
        List<RideRequestSent> rideRequest = RideRequestSent.find(RideRequestSent.class, "ride_id = ?", rideId);
        if (rideRequest == null || rideRequest.isEmpty())
            return null;
        String date = rideRequest.get(0).getDate();
        String going = rideRequest.get(0).isGoing() ? "true" : "false";
        rideRequest = RideRequestSent.find(RideRequestSent.class, "date = ? and going = ?", date, going);
        if (rideRequest == null || rideRequest.isEmpty())
            return null;

        ArrayList<RideIdForJson> rideIdsList = new ArrayList<>();
        for (RideRequestSent rideRequest1 : rideRequest) {
            rideIdsList.add(new RideIdForJson(rideRequest1.getDbId()));
        }

        App.getNetworkService().deleteJoinRequests(rideIdsList, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i("deleteJoinRequests", "requests deleted");
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    Log.e("deleteJoinRequests", error.getMessage());
                } catch (Exception e) {//sometimes RetrofitError is null
                    Log.e("deleteJoinRequests", e.getMessage());
                }
            }
        });

        return null;
    }
}
