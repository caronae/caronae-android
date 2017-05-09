package br.ufrj.caronae.frags;

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
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.MyActiveRidesAdapter;
import br.ufrj.caronae.adapters.MyRidesAdapter;
import br.ufrj.caronae.comparators.RideComparatorByDateAndTime;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.modelsforjson.LoginForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.UserWithRidesForJson;
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
            }
            if (allRides.size() == 0) {
                norides_tv.setVisibility(View.VISIBLE);
            } else {
                updateAdapter();
            }
        }
    }

    public void loadMyOfferRides() {
        App.getNetworkService(App.inst()).login(new LoginForJson(SharedPref.getUserToken(), SharedPref.getUserIdUfrj())).enqueue(new Callback<UserWithRidesForJson>() {
            @Override
            public void onResponse(Call<UserWithRidesForJson> call, Response<UserWithRidesForJson> response) {
                if (response.isSuccessful()) {
                    UserWithRidesForJson userWithRides = response.body();
                    if (!(userWithRides.getRides() == null)) {

                        Ride.deleteAll(Ride.class);

                        for (Ride ride : userWithRides.getRides()) {
                            ride.setTime(Util.formatTime(ride.getTime()));
                            String format = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
                            int c = ride.getDate().compareTo(format);
                            if (c >= 0)
                                if (Ride.findById(Ride.class, ride.getDbId()) == null) {
                                    new Ride(ride).save();
                                }
                        }
                    }
                    new LoadRides().execute();
                } else
                    new LoadRides().execute();
            }

            @Override
            public void onFailure(Call<UserWithRidesForJson> call, Throwable t) {
                new LoadRides().execute();
            }
        });
    }

    private void getActiveRides() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
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

                                        loadMyOfferRides();
//                                        new LoadRides().execute();
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

                                    MyRidesFrag.hideProgressBar();
                                } else {
                                    Util.treatResponseFromServer(response);
                                    MyRidesFrag.hideProgressBar();

                                    norides_tv.setVisibility(View.VISIBLE);
                                    Util.toast(R.string.frag_myactiverides_errorGetActiveRides);

                                    Log.e("getMyActiveRides", response.message());
                                }
//                                new LoadRides().execute();
                                loadMyOfferRides();
                            }

                            @Override
                            public void onFailure(Call<List<RideForJson>> call, Throwable t) {
                                MyRidesFrag.hideProgressBar();
                                norides_tv.setVisibility(View.VISIBLE);
                                Util.toast(R.string.frag_myactiverides_errorGetActiveRides);

//                                new LoadRides().execute();
                                loadMyOfferRides();
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
        allRides = new ArrayList<>();


        for (int ridesIndex = 0; ridesIndex < rideWithUsersList.size(); ridesIndex++) {
            if (rideWithUsersList.get(ridesIndex).isGoing() == going) {
                allRides.add(rideWithUsersList.get(ridesIndex));
            }
        }
    }

    private void addAllMyRidesToList(List<Ride> rides) {

        for (int ridesIndex = 0; ridesIndex < rides.size(); ridesIndex++) {
            if (rides.get(ridesIndex).isGoing() == going) {
                allRides.add(rides.get(ridesIndex));
            }
        }
    }
}
