package br.ufrj.caronae.frags;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.CustomDateTimePicker;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideSearchFiltersForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideSearchFrag extends Fragment {

    @BindView(R.id.tab1)
    RelativeLayout isGoing_bt;
    @BindView(R.id.tab2)
    RelativeLayout isLeaving_bt;
    @BindView(R.id.tab1_tv)
    TextView isGoing_tv;
    @BindView(R.id.tab2_tv)
    TextView isLeaving_tv;

    @BindView(R.id.center_et)
    TextView center_et;
    @BindView(R.id.location_et)
    TextView location_et;
    @BindView(R.id.time_et)
    public TextView time_et;

    @BindView(R.id.lay)
    RelativeLayout lay;

    @BindView(R.id.anotherSearch_bt)
    Button anotherSearch_bt;

    @BindView(R.id.rvRides)
    RecyclerView rvRides;

    private RideOfferAdapter adapter;

    private String neighborhoods;
    private boolean going;
    public String time;

    public RideSearchFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_ride_search, container, false);
        ButterKnife.bind(this, view);

        going = true;
        setButton(isLeaving_bt,isGoing_bt, isLeaving_tv, isGoing_tv);
        if(SharedPref.getGoingLabel() != null)
        {
            isGoing_tv.setText(SharedPref.getGoingLabel());
        }
        if(SharedPref.getLeavingLabel() != null)
        {
            isLeaving_tv.setText(SharedPref.getLeavingLabel());
        }

        setInitialDate();

        adapter = new RideOfferAdapter(new ArrayList<RideForJson>(), getActivity(), getActivity().getFragmentManager());
        rvRides.setAdapter(adapter);
        rvRides.setHasFixedSize(true);
        rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

        String lastRideSearchFilters = SharedPref.getLastRideSearchFiltersPref();

        if (!lastRideSearchFilters.equals(SharedPref.MISSING_PREF))
        {
            loadLastFilters(lastRideSearchFilters);
        }

        App.getBus().register(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        App.getBus().unregister(this);
    }

    @Override
    public void onStart()
    {
        Util.debug(SharedPref.LOCATION_INFO);
        if(!SharedPref.LOCATION_INFO.isEmpty() && !SharedPref.LOCATION_INFO.equals(""))
        {
            location_et.setText(SharedPref.LOCATION_INFO);
            SharedPref.LOCATION_INFO = "";
        }
        super.onStart();
    }

    @Override
    public void onResume()
    {
        if(!SharedPref.LOCATION_INFO.isEmpty() && !SharedPref.LOCATION_INFO.equals(""))
        {
            location_et.setText(SharedPref.LOCATION_INFO);
            SharedPref.LOCATION_INFO = "";
        }
        super.onResume();
    }

    private void loadLastFilters(String lastRideSearchFilters) {
        RideSearchFiltersForJson rideSearchFilters = new Gson().fromJson(lastRideSearchFilters, RideSearchFiltersForJson.class);
        location_et.setText(rideSearchFilters.getLocationResumedField());
        neighborhoods = rideSearchFilters.getLocation();
        time_et.setText(rideSearchFilters.getTime());
        center_et.setText(rideSearchFilters.getCenter());
        going = rideSearchFilters.isGo();
        if(going)
        {
            setButton(isLeaving_bt, isGoing_bt,isLeaving_tv, isGoing_tv);
        }
        else
        {
            setButton(isGoing_bt, isLeaving_bt, isGoing_tv, isLeaving_tv);
        }
    }

    @OnClick(R.id.location_et)
    public void locationEt() {
        Intent intent = new Intent(getActivity(), PlaceAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("backText", "Buscar");
        intent.putExtra("allP", true);
        intent.putExtra("otherP", true);
        intent.putExtra("getBack", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.time_et)
    public void timeEt() {
        Activity activity = getActivity();
        CustomDateTimePicker cdtp;
        if(going) {
            if(SharedPref.getGoingLabel() == null)
                cdtp = new CustomDateTimePicker(activity, getResources().getString(R.string.go_rb), time, this, "Search");
            else
                cdtp = new CustomDateTimePicker(activity, SharedPref.getGoingLabel(), time, this, "Search");
        }else
        {
            if(SharedPref.getLeavingLabel() == null)
                cdtp = new CustomDateTimePicker(activity, getResources().getString(R.string.back_rb), time, this, "Search");
            else
                cdtp = new CustomDateTimePicker(activity, SharedPref.getLeavingLabel(), time, this, "Search");
        }
        Window window = cdtp.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(cdtp.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320, getResources().getDisplayMetrics());
        cdtp.show();
        cdtp.getWindow().setAttributes(lp);
    }

    @OnClick(R.id.center_et)
    public void centerEt() {

    }

    @OnClick(R.id.search_bt)
    public void searchBt()
    {
        String location = location_et.getText().toString();

        if (location.contains("+"))
        {
            location = neighborhoods;
        }

        //sexta-feira, 11/05/2018 07:00
        //01234567890123456789012345678

        String etDateString = time_et.getText().toString().substring(time_et.getText().toString().length()-17, time_et.getText().toString().length()-7);
        String time = time_et.getText().toString().substring(time_et.getText().toString().length()-6);

        String center = center_et.getText().toString();

        if (center.equals(Util.getFundaoCenters()[0]) || center.equals("Cidade Universitária"))
            center = "";

        String campus = "";

        RideSearchFiltersForJson rideSearchFilters = new RideSearchFiltersForJson(location, etDateString, time, center, campus, going, location_et.getText().toString());

        String lastRideSearchFilters = new Gson().toJson(rideSearchFilters);
        SharedPref.saveLastRideSearchFiltersPref(lastRideSearchFilters);
        rideSearchFilters.setDate(Util.formatBadDateWithYear(etDateString));

        Log.e("INPUT", "location: " + location);
        Log.e("INPUT", "data: " + etDateString);
        Log.e("INPUT", "hora: " + time);
        Log.e("INPUT", "center: " + center);
        Log.e("INPUT", "campus: " + campus);
        Log.e("INPUT", "locationResumeField: " + location_et.getText().toString());

        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", getContext().getString(R.string.wait), true, true);
        CaronaeAPI.service(getContext()).listFiltered(rideSearchFilters)
            .enqueue(new Callback<List<RideForJson>>() {
                @Override
                public void onResponse(Call<List<RideForJson>> call, Response<List<RideForJson>> response) {
                    if (response.isSuccessful()) {
                        List<RideForJson> rideOffers = response.body();
                        if (rideOffers != null && !rideOffers.isEmpty()) {
                            Util.expandOrCollapse(lay, false);
                            anotherSearch_bt.setVisibility(View.VISIBLE);
                            Collections.sort(rideOffers, new RideOfferComparatorByDateAndTime());
                            for (RideForJson rideOffer : rideOffers) {
                                rideOffer.setDbId(rideOffer.getId().intValue());
                            }
                            adapter.makeList(rideOffers);
                        } else {
                            Util.treatResponseFromServer(response);
                            Util.toast(R.string.frag_rideSearch_noRideFound);
                            adapter.makeList(new ArrayList<RideForJson>());
                        }
                        pd.dismiss();
                    } else {
                        pd.dismiss();
                        Util.toast(R.string.frag_rideSearch_errorListFiltered);
                        Log.e("listFiltered", response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<RideForJson>> call, Throwable t) {
                    pd.dismiss();
                    Util.toast(R.string.frag_rideSearch_errorListFiltered);
                    Log.e("listFiltered", t.getMessage());
                }
            });
    }

    @OnClick(R.id.anotherSearch_bt)
    public void anotherSearchBt() {
        Util.expandOrCollapse(lay, true);
        anotherSearch_bt.setVisibility(View.GONE);
    }

    @OnClick(R.id.tab1)
    public void goingTabSelected()
    {
        if(!going)
        {
            going = true;
            setButton(isLeaving_bt, isGoing_bt,isLeaving_tv, isGoing_tv);
        }
    }

    @OnClick(R.id.tab2)
    public void leavingTabSelected()
    {
        if(going)
        {
            going = false;
            setButton(isGoing_bt, isLeaving_bt, isGoing_tv, isLeaving_tv);
        }
    }

    private void setButton(RelativeLayout button1, RelativeLayout button2, TextView bt1_tv, TextView bt2_tv)
    {
        button1.setFocusable(true);
        button1.setClickable(true);
        button2.setFocusable(false);
        button2.setClickable(false);
        GradientDrawable bt1Shape = (GradientDrawable)button1.getBackground();
        GradientDrawable bt2Shape = (GradientDrawable)button2.getBackground();
        bt1Shape.setColor(getResources().getColor(R.color.white));
        bt2Shape.setColor(getResources().getColor(R.color.dark_gray));
        bt1_tv.setTextColor(getResources().getColor(R.color.dark_gray));
        bt2_tv.setTextColor(getResources().getColor(R.color.white));
    }

    @Subscribe
    public void removeRideFromList(RideRequestSent ride) {
        adapter.remove(ride.getDbId());
        Log.i("removeRideFromList,srch", "remove called");
    }

    private void setInitialDate()
    {
        Calendar rightNow = Calendar.getInstance();
        Date date = rightNow.getTime();
        SimpleDateFormat dateWithYear = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String ddmmyyyy = dateWithYear.format(date);
        String weekday = Util.getWeekDayFromBRDate(ddmmyyyy);
        int hourInt = rightNow.get(Calendar.HOUR_OF_DAY);
        int minuteInt = rightNow.get(Calendar.MINUTE) + 5;
        if(minuteInt >= 60)
        {
            hourInt += 2;
        }
        else{
            hourInt += 1;
        }

        if(hourInt >= 24)
        {
            hourInt -= 24;
            rightNow.add(Calendar.DAY_OF_YEAR, 1);
            date = rightNow.getTime();
            ddmmyyyy = dateWithYear.format(date);
        }

        if(hourInt < 10)
        {
            time = ddmmyyyy + " 0" + hourInt + ":00";
        }
        else
        {
            time = ddmmyyyy + " " + hourInt + ":00";
        }
        String result = weekday + ", " + time;
        time_et.setText(result);
    }

    {
    /*private void showDefaultMultichoiceList(final boolean[] mSelectedItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final ArrayList<String> selectedItems = new ArrayList<>();
        for (int i = 0; i < mSelectedItems.length; i++){
            if (mSelectedItems[i])
                selectedItems.add(Util.getFundaoCenters()[i]);
        }
        builder.setTitle("CENTROS")
                .setMultiChoiceItems(Util.getFundaoCenters(), mSelectedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    selectedItems.add(Util.getFundaoCenters()[which]);
                                } else if (selectedItems.contains(Util.getFundaoCenters()[which])) {
                                    selectedItems.remove(Util.getFundaoCenters()[which]);
                                }
                            }
                        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String centers = "";
                        if (selectedItems.size() == 9 || mSelectedItems[0]){
                            centers = "Cidade Universitária";
                        } else {
                            for (int i = 0; i < selectedItems.size(); i++) {
                                centers = centers + selectedItems.get(i) + ", ";
                            }
                            if (centers.length() > 0) {
                                centers = centers.substring(0, centers.length() - 2);
                            }
                        }
                        center_et.setText(centers);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.show();
    }*/

    /*public void locationEt2(final String zone) {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                CharSequence[] selectedNeighborhoods = null;
                try {
                    selectedNeighborhoods = getSelectedValues();
                } catch (Exception e) {
                    //do nothing
                }
                if (selectedNeighborhoods != null) {
                    String resumedField = "";
                    neighborhoods = "";
                    for (int i = 0; i < selectedNeighborhoods.length; i++) {
                        if (selectedNeighborhoods[i].equals(Util.getNeighborhoods("")[0])) {
                            super.onPositiveActionClicked(fragment);
                            return;
                        }
                        neighborhoods += selectedNeighborhoods[i];
                        if (i == 2) {
                            resumedField = neighborhoods + " + " + (selectedNeighborhoods.length - 3);
                        }
                        if (i + 1 != selectedNeighborhoods.length) {
                            neighborhoods += ", ";
                        }
                    }

                    if (selectedNeighborhoods.length > 3) {
                        location_et.setText(resumedField);
                    } else {
                        location_et.setText(neighborhoods);
                    }

                } else {
                    location_et.setText(zone);
                }
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        @SuppressWarnings("ConstantConditions")
        ArrayList<String> neighborhoods = new ArrayList<>(Arrays.asList(Util.getNeighborhoods(zone)));
        neighborhoods.add(0, Util.getNeighborhoods("")[0]);
        String[] neighborhoodsArray = new String[neighborhoods.size()];
        neighborhoods.toArray(neighborhoodsArray);
        builder.multiChoiceItems(neighborhoodsArray, -1)
                .title(getContext().getString(R.string.frag_rideSearch_pickNeighborhood))
                .positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }*/
    }
}