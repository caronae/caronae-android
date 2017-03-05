package br.ufrj.caronae.frags;

import android.app.ProgressDialog;
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
import br.ufrj.caronae.adapters.MyActiveRidesAdapter;
import br.ufrj.caronae.adapters.MyRidesAdapter;
import br.ufrj.caronae.comparators.RideComparatorByDateAndTime;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.Ride;
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
        Util.OffsetDecoration OffsetDecoration = new Util.OffsetDecoration((int) offsetBottonPx, (int)offsetTopPx);

        myRidesList.addItemDecoration(OffsetDecoration);

        new LoadRides().execute();

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

            getActiveRides();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!rides.isEmpty()) {
                Collections.sort(rides, new RideComparatorByDateAndTime());

                for (int i = 0; i < rides.size(); i++){
                    Log.e("ERROu", "My RIdes: " + rides.get(i).getHub());
                }

                addAllMyRidesToList(rides);

//                deleteAll_bt.setVisibility(View.VISIBLE);
            } else {
                norides_tv.setVisibility(View.VISIBLE);
            }
        }
    }

//    @OnClick(R.id.deleteAll_bt)
//    public void deleteAllBt() {
//        if (rides == null || rides.isEmpty())
//            return;
//
//        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
//
//            @Override
//            protected void onBuildDone(Dialog dialog) {
//                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            }
//
//            @Override
//            public void onPositiveActionClicked(DialogFragment fragment) {
//                final ProgressDialog pd = ProgressDialog.show(getContext(), "", getResources().getString(R.string.wait), true, true);
//
//                App.getNetworkService(getContext()).deleteAllRidesFromUser("stub", going)
//                        .enqueue(new Callback<ResponseBody>() {
//                            @Override
//                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                if(response.isSuccessful()){
//                                    Log.i("deleteAllRidesFromUser", "all rides deleted");
//
//                                    Ride.deleteAll(Ride.class, "going = ?", going ? "1" : "0");
//                                    Util.toast(R.string.frag_myrides_ridesDeleted);
//                                    rides.clear();
//                                    myRidesList.getAdapter().notifyDataSetChanged();
//                                    norides_tv.setVisibility(View.VISIBLE);
//                                    deleteAll_bt.setVisibility(View.INVISIBLE);
//
//                                    pd.dismiss();
//                                } else {
//                                    Util.toast(getString(R.string.frag_myrides_errorDeleteAllRIdes));
//                                    Log.e("deleteRide", response.message());
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                Util.toast(getString(R.string.frag_myrides_errorDeleteAllRIdes));
//                                Log.e("deleteRide", t.getMessage());
//                            }
//                        });
//                super.onPositiveActionClicked(fragment);
//            }
//
//            @Override
//            public void onNegativeActionClicked(DialogFragment fragment) {
//                super.onNegativeActionClicked(fragment);
//            }
//        };
//
//        ((SimpleDialog.Builder) builder).message(getString(R.string.warnDeleteRidesCouldBeActive))
//                .title(getString(R.string.attention))
//                .positiveAction(getString(R.string.ok))
//                .negativeAction(getString(R.string.cancel));
//
//        DialogFragment fragment = DialogFragment.newInstance(builder);
//        fragment.show(getActivity().getSupportFragmentManager(), null);
//    }

    private void getActiveRides(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ProgressDialog pd = ProgressDialog.show(getContext(), "", getActivity().getString(R.string.wait), true, true);
                App.getNetworkService(getContext()).getMyActiveRides()
                        .enqueue(new Callback<List<RideForJson>>() {
                            @Override
                            public void onResponse(Call<List<RideForJson>> call, Response<List<RideForJson>> response) {

                                if (response.isSuccessful()){

                                    List<RideForJson> rideWithUsersList = response.body();
                                    if (rideWithUsersList == null || rideWithUsersList.isEmpty()) {
                                        pd.dismiss();

                                        myRidesList.setAdapter(new MyActiveRidesAdapter(new ArrayList<RideForJson>(), (MainAct) getActivity()));
                                        myRidesList.setHasFixedSize(true);
                                        myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));

                                        norides_tv.setVisibility(View.VISIBLE);
                                        return;
                                    }

                                    ActiveRide.deleteAll(ActiveRide.class);
                                    //subscribe to ride id topic
                                    for (RideForJson rideWithUsers : rideWithUsersList) {
                                        int rideId = rideWithUsers.getId().intValue();
                                        rideWithUsers.setDbId(rideId);

                                        FirebaseTopicsHandler.subscribeFirebaseTopic(rideId + "");

                                        new ActiveRide(rideWithUsers.getDbId(),rideWithUsers.isGoing(), rideWithUsers.getDate()).save();
                                    }

                                    Collections.sort(rideWithUsersList, new RideOfferComparatorByDateAndTime());
                                    addAllActiveRidesToList(rideWithUsersList);
                                    updateAdapter();

                                    for (int i = 0; i < rideWithUsersList.size(); i++) {
                                        Log.e("ERROu", "My Active RIdes: " + rideWithUsersList.get(i).getId());
                                    }

                                    pd.dismiss();
                                } else {
                                    pd.dismiss();

                                    norides_tv.setVisibility(View.VISIBLE);
                                    Util.toast(R.string.frag_myactiverides_errorGetActiveRides);

                                    Log.e("getMyActiveRides", response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<List<RideForJson>> call, Throwable t) {
                                pd.dismiss();

                                norides_tv.setVisibility(View.VISIBLE);
                                Util.toast(R.string.frag_myactiverides_errorGetActiveRides);

                                Log.e("getMyActiveRides", t.getMessage());
                            }
                        });
            }
        });
    }

    private void updateAdapter(){
        adapter = new MyRidesAdapter(allRides, (MainAct) getActivity());
        myRidesList.setAdapter(adapter);
        myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void addAllActiveRidesToList(List<RideForJson> rideWithUsersList){
        for (int allRidesIndex = 0; allRidesIndex < allRides.size(); allRidesIndex++){
            if (allRides.get(allRidesIndex).getClass() == RideForJson.class){
                allRides.remove(allRidesIndex);
            }
        }
//        allRides.addAll(rideWithUsersList);
        for (int ridesIndex = 0; ridesIndex < rideWithUsersList.size(); ridesIndex++) {
            if (rideWithUsersList.get(ridesIndex).isGoing() == going) {
                allRides.add(rideWithUsersList.get(ridesIndex));
            }
        }
    }

    private void addAllMyRidesToList(List<Ride> rides){
        for (int allRidesIndex = 0; allRidesIndex < allRides.size(); allRidesIndex++){
            if (allRides.get(allRidesIndex).getClass() == RideForJson.class){
                allRides.remove(allRidesIndex);
            }
        }
        allRides.addAll(rides);
    }
}
