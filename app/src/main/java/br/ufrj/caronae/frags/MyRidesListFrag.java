package br.ufrj.caronae.frags;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.acts.RequestersListAct;
import br.ufrj.caronae.adapters.MyActiveRidesAdapter;
import br.ufrj.caronae.adapters.MyRidesAdapter;
import br.ufrj.caronae.comparators.RideComparatorByDateAndTime;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.NewChatMsgIndicator;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideRequestReceived;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRidesListFrag extends Fragment {

    @Bind(R.id.myRidesList)
    RecyclerView myRidesList;
    @Bind(R.id.norides_tv)
    TextView norides_tv;
//    @Bind(R.id.deleteAll_bt)
//    Button deleteAll_bt;


    ArrayList<Ride> rides;
    ArrayList<Object> allRides;
    private boolean going;

    MyRidesAdapter adapter;

    public MyRidesListFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rides_list, container, false);
        ButterKnife.bind(this, view);

        allRides = new ArrayList<>();

        Bundle bundle = getArguments();
        going = bundle.getBoolean("going");

        float offsetBottonPx = getResources().getDimension(R.dimen.recycler_view_botton_offset_my_rides);
        float offsetTopPx = getResources().getDimension(R.dimen.recycler_view_top_offset);
        Util.OffsetDecoration OffsetDecoration = new Util.OffsetDecoration((int) offsetBottonPx, (int) offsetTopPx);

        myRidesList.addItemDecoration(OffsetDecoration);

        getActiveRides();

        return view;
    }


    //TODO: Remove Deprecated functions
    public class TimeIgnoringComparator implements Comparator<Date> {
        public int compare(Date d1, Date d2) {
            if (d1.getYear() != d2.getYear())
                return d1.getYear() - d2.getYear();
            if (d1.getMonth() != d2.getMonth())
                return d1.getMonth() - d2.getMonth();
            return d1.getDate() - d2.getDate();
        }
    }

    public class LoadRides extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... arg0) {
            rides = new ArrayList<>();
            rides = (ArrayList<Ride>) Ride.find(Ride.class, "going = ?", going ? "1" : "0");

            List<RideRequestReceived> rideRequestReceivedList = RideRequestReceived.listAll(RideRequestReceived.class);

//            boolean checkIfRequested = false;
//            for (int rideIndex = 0; rideIndex < rides.size(); rideIndex++){
//                checkIfRequested  = true;
//                for (int rideRequestIndex = 0; rideRequestIndex < rideRequestReceivedList.size(); rideRequestIndex++){
//                    if (rides.get(rideIndex).getDbId() == rideRequestReceivedList.get(rideRequestIndex).getDbId()){
//                        checkIfRequested = false;
//                    }
//                }
//                if (checkIfRequested){
//                    checkRequesters(rides.get(rideIndex).getDbId());
//                }
//            }


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date todayDate = new Date();

            Iterator<Ride> it = rides.iterator();
            while (it.hasNext()) {
                Ride ride = it.next();
                try {
                    Date rideDate = simpleDateFormat.parse(ride.getDate());
                    if (new TimeIgnoringComparator().compare(rideDate, todayDate) < 0) {
                        ride.delete();
                        it.remove();
                    }
                } catch (Exception e) {
                    Log.e("LoadRides", e.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!rides.isEmpty()) {
                Collections.sort(rides, new RideComparatorByDateAndTime());
                addAllMyRidesToList(rides);

//                deleteAll_bt.setVisibility(View.VISIBLE);
            }
            if (allRides.size() == 0) {
                norides_tv.setVisibility(View.VISIBLE);
            } else {
                updateAdapter();
            }
        }
    }

//    private void checkRequesters(final int dbId) {
//        App.getNetworkService(getActivity().getApplicationContext()).getRequesters(dbId + "")
//                .enqueue(new Callback<List<User>>() {
//                    @Override
//                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
//                        if (response.isSuccessful()) {
//                            new RideRequestReceived(Integer.valueOf(dbId)).save();
//                            updateAdapter();
//                        } else {
//                            Util.toast(R.string.errorGetRequesters);
//                            Log.e("getRequesters", response.message());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<User>> call, Throwable t) {
//                        Util.toast(R.string.errorGetRequesters);
//                        Log.e("getRequesters", t.getMessage());
//                    }
//                });
//    }


    private void getActiveRides() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                final ProgressDialog pd = ProgressDialog.show(getContext(), "", getActivity().getString(R.string.wait), true, true);
                App.getNetworkService(getContext()).getMyActiveRides()
                        .enqueue(new Callback<List<RideForJson>>() {
                            @Override
                            public void onResponse(Call<List<RideForJson>> call, Response<List<RideForJson>> response) {

                                if (response.isSuccessful()) {

                                    List<RideForJson> rideWithUsersList = response.body();
                                    if (rideWithUsersList == null || rideWithUsersList.isEmpty()) {
                                        MyRidesFrag.hideProgressBar();

                                        myRidesList.setAdapter(new MyActiveRidesAdapter(new ArrayList<RideForJson>(), (MainAct) getActivity()));
                                        myRidesList.setHasFixedSize(true);
                                        myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));

//                                        norides_tv.setVisibility(View.VISIBLE);
                                        new LoadRides().execute();
                                        return;
                                    }

                                    ActiveRide.deleteAll(ActiveRide.class);
                                    //subscribe to ride id topic
                                    for (RideForJson rideWithUsers : rideWithUsersList) {
                                        int rideId = rideWithUsers.getId().intValue();
                                        rideWithUsers.setDbId(rideId);

                                        FirebaseTopicsHandler.subscribeFirebaseTopic(rideId + "");

                                        new ActiveRide(rideWithUsers.getDbId(), rideWithUsers.isGoing(), rideWithUsers.getDate()).save();
                                    }

                                    Collections.sort(rideWithUsersList, new RideOfferComparatorByDateAndTime());
                                    addAllActiveRidesToList(rideWithUsersList);

//                                    pd.dismiss();
                                    MyRidesFrag.hideProgressBar();
                                } else {
                                    Util.treatResponseFromServer(response);
                                    MyRidesFrag.hideProgressBar();
//                                    pd.dismiss();

                                    norides_tv.setVisibility(View.VISIBLE);
                                    Util.toast(R.string.frag_myactiverides_errorGetActiveRides);

                                    Log.e("getMyActiveRides", response.message());
                                }
                                new LoadRides().execute();
                            }

                            @Override
                            public void onFailure(Call<List<RideForJson>> call, Throwable t) {
//                                pd.dismiss();
                                MyRidesFrag.hideProgressBar();
                                norides_tv.setVisibility(View.VISIBLE);
                                Util.toast(R.string.frag_myactiverides_errorGetActiveRides);

                                new LoadRides().execute();

                                Log.e("getMyActiveRides", t.getMessage());
                            }
                        });
            }
        });
    }

    private void updateAdapter() {
        adapter = new MyRidesAdapter(allRides, (MainAct) getActivity());
        myRidesList.setAdapter(adapter);
        myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void addAllActiveRidesToList(List<RideForJson> rideWithUsersList) {
//        for (int allRidesIndex = 0; allRidesIndex < allRides.size(); allRidesIndex++) {
//            if (allRides.get(allRidesIndex).getClass() == RideForJson.class) {
//                allRides.remove(allRidesIndex);
//            }
//        }

        allRides = new ArrayList<>();
//        allRides.addAll(rideWithUsersList);


        for (int ridesIndex = 0; ridesIndex < rideWithUsersList.size(); ridesIndex++) {
            if (rideWithUsersList.get(ridesIndex).isGoing() == going) {
                allRides.add(rideWithUsersList.get(ridesIndex));
            }
        }
    }

    private void addAllMyRidesToList(List<Ride> rides) {
//        for (int allRidesIndex = 0; allRidesIndex < allRides.size(); allRidesIndex++) {
//            if (allRides.get(allRidesIndex).getClass() == RideForJson.class) {
//                allRides.remove(allRidesIndex);
//            }
//        }

        for (int ridesIndex = 0; ridesIndex < rides.size(); ridesIndex++) {
            if (rides.get(ridesIndex).isGoing() == going) {
                allRides.add(rides.get(ridesIndex));
            }
        }

//        allRides.addAll(rides);
    }
}
