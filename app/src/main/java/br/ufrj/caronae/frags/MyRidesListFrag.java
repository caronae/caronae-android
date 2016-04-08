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

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.MyRidesAdapter;
import br.ufrj.caronae.comparators.RideComparatorByDateAndTime;
import br.ufrj.caronae.models.Ride;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyRidesListFrag extends Fragment {

    @Bind(R.id.myRidesList)
    RecyclerView myRidesList;
    @Bind(R.id.norides_tv)
    TextView norides_tv;
    @Bind(R.id.deleteAll_bt)
    Button deleteAll_bt;
    @Bind(R.id.helpText_tv)
    TextView helpText_tv;

    ArrayList<Ride> rides;
    private boolean going;

    public MyRidesListFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rides_list, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        going = bundle.getBoolean("going");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        new LoadRides().execute();
    }

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

                myRidesList.setAdapter(new MyRidesAdapter(rides, (MainAct) getActivity()));
                myRidesList.setHasFixedSize(true);
                myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));

                deleteAll_bt.setVisibility(View.VISIBLE);
                helpText_tv.setVisibility(View.VISIBLE);
            } else {
                norides_tv.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.deleteAll_bt)
    public void deleteAllBt() {
        if (rides == null || rides.isEmpty())
            return;

        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                final ProgressDialog pd = ProgressDialog.show(getContext(), "", getResources().getString(R.string.wait), true, true);

                App.getNetworkService().deleteAllRidesFromUser(going, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Log.i("deleteAllRidesFromUser", "all rides deleted");

                        Ride.deleteAll(Ride.class);
                        Util.toast(R.string.frag_myrides_ridesDeleted);
                        rides.clear();
                        myRidesList.getAdapter().notifyDataSetChanged();
                        norides_tv.setVisibility(View.VISIBLE);
                        deleteAll_bt.setVisibility(View.INVISIBLE);
                        helpText_tv.setVisibility(View.INVISIBLE);

                        pd.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Util.toast(getString(R.string.frag_myrides_errorDeleteAllRIdes));
                        try {
                            Log.e("deleteRide", error.getMessage());
                        } catch (Exception e) {//sometimes RetrofitError is null
                            Log.e("deleteRide", e.getMessage());
                        }

                        pd.dismiss();
                    }
                });

                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        ((SimpleDialog.Builder) builder).message(getString(R.string.warnDeleteRidesCouldBeActive))
                .title(getString(R.string.attention))
                .positiveAction(getString(R.string.ok))
                .negativeAction(getString(R.string.cancel));

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getActivity().getSupportFragmentManager(), null);
    }
}
