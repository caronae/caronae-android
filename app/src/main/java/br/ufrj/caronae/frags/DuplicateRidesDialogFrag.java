package br.ufrj.caronae.frags;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideRountine;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Luis on 6/8/2017.
 */

public class DuplicateRidesDialogFrag extends DialogFragment {

    boolean duplicate;
    ProgressDialog pd;
    static Ride ride;

    static DuplicateRidesDialogFrag newInstance(Ride rideToCreate) {
        DuplicateRidesDialogFrag f = new DuplicateRidesDialogFrag();

        ride = rideToCreate;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean duplicate = getArguments().getBoolean("duplicate");

        View v;
        if (duplicate) {
            v = inflater.inflate(R.layout.duplicate_rides_dialog, container, false);
//            Button cancel = (Button) v.findViewById(R.id.cancel_button);
//            Button create = (Button) v.findViewById(R.id.create_button);

//            cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dismiss();
//                }
//            });
//
//            create.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    createRide(ride);
//                }
//            });
        } else {
            v = inflater.inflate(R.layout.possible_duplicate_rides_dialog, container, false);
        }
        return v;
    }

    private void createRide(Ride ride) {
        pd = ProgressDialog.show(getContext(), "", getString(R.string.wait), true, true);
        App.getNetworkService(getContext()).offerRide(ride)
                .enqueue(new Callback<List<RideRountine>>() {
                    @Override
                    public void onResponse(Call<List<RideRountine>> call, Response<List<RideRountine>> response) {
                        if (response.isSuccessful()) {

                            List<RideRountine> rideRountines = response.body();
                            List<Ride> rides = new ArrayList<Ride>();
                            for (RideRountine rideRountine : rideRountines) {
                                rides.add(new Ride(rideRountine));
                            }

                            for (Ride ride : rides) {
                                Ride ride2 = new Ride(ride);
                                ride2.setDbId(ride.getId().intValue());
                                FirebaseTopicsHandler.subscribeFirebaseTopic(String.valueOf(ride.getId().intValue()));
                                ride2.save();
                                Util.createChatAssets(ride2, getContext());
                            }
                            pd.dismiss();
                            ((MainAct) getActivity()).removeFromBackstack(RideOfferFrag.class);
                            ((MainAct) getActivity()).showActiveRidesFrag();
                            Util.toast(R.string.frag_rideOffer_rideSaved);
                        } else {
                            Util.treatResponseFromServer(response);
                            pd.dismiss();
                            if (response.code() == 403) {
                                Util.toast(R.string.past_ride_creation);
                            } else {
                                Util.toast(R.string.frag_rideOffer_errorRideSaved);
                                Log.e("offerRide", response.message());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RideRountine>> call, Throwable t) {
                        pd.dismiss();
                        Util.toast(R.string.frag_rideOffer_errorRideSaved);
                        Log.e("offerRide", t.getMessage());
                    }
                });
    }
}
