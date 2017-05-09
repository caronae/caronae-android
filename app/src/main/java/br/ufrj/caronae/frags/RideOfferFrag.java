package br.ufrj.caronae.frags;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.TimePickerDialog;
import com.rey.material.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideRountine;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideOfferFrag extends Fragment {

    private final String ERROR_MESSAGE = "java.lang.IllegalStateException: Expected a string but was BEGIN_OBJECT at line 1 column 177 path $[0].repeats_until";

    @Bind(R.id.radioGroup2)
    RadioGroup radioGroup2;

    @Bind(R.id.neighborhood_et)
    EditText neighborhood_et;
    @Bind(R.id.place_et)
    EditText place_et;
    @Bind(R.id.way_et)
    EditText way_et;
    @Bind(R.id.date_et)
    TextView date_et;
    @Bind(R.id.time_et)
    TextView time_et;
    @Bind(R.id.slots_et)
    Spinner slots_et;
    @Bind(R.id.center_et)
    EditText center_et;
    @Bind(R.id.description_et)
    EditText description_et;

    @Bind(R.id.routine_cb)
    CheckBox routine_cb;
    @Bind(R.id.days_lo)
    RelativeLayout days_lo;

    @Bind(R.id.monday_cb)
    CheckBox monday_cb;
    @Bind(R.id.tuesday_cb)
    CheckBox tuesday_cb;
    @Bind(R.id.wednesday_cb)
    CheckBox wednesday_cb;
    @Bind(R.id.thursday_cb)
    CheckBox thursday_cb;
    @Bind(R.id.friday_cb)
    CheckBox friday_cb;
    @Bind(R.id.saturday_cb)
    CheckBox saturday_cb;
    @Bind(R.id.sunday_cb)
    CheckBox sunday_cb;
    @Bind(R.id.scrollView)
    ScrollView scrollView;

    private String zone;
    private boolean going;

    public RideOfferFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_offer, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        going = bundle.getBoolean("going");

        center_et.setHint(going ? R.string.frag_rideSearch_hintPickCenter : R.string.frag_rideOffer_hintPickHub);

        String[] items = new String[6];
        for (int i = 0; i < items.length; i++)
            items[i] = String.valueOf(i + 1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.row_spn, items);
        adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
        slots_et.setAdapter(adapter);

        String lastRideOffer = going ? SharedPref.getLastRideGoingPref() : SharedPref.getLastRideNotGoingPref();
        if (!lastRideOffer.equals(SharedPref.MISSING_PREF)) {
            loadLastRide(lastRideOffer);
        }

        checkCarOwnerDialog();

        return view;
    }

    private boolean checkCarOwnerDialog() {
        if (!App.getUser().isCarOwner()) {
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.notCarOwner)
                    .show();
            return false;
        }

        return true;
    }

    private void loadLastRide(String lastRideOffer) {
        Ride ride = new Gson().fromJson(lastRideOffer, Ride.class);
        zone = ride.getZone();
        neighborhood_et.setText(ride.getNeighborhood());
        place_et.setText(ride.getPlace());
        way_et.setText(ride.getRoute());
        date_et.setText(ride.getDate());
        time_et.setText(ride.getTime());
        slots_et.setSelection(Integer.parseInt(ride.getSlots()) - 1);
        center_et.setText(ride.getHub());
        description_et.setText(ride.getDescription());
        boolean isRoutine = ride.isRoutine();
        routine_cb.setChecked(isRoutine);
        if (isRoutine) {
            days_lo.setVisibility(routine_cb.isChecked() ? View.VISIBLE : View.GONE);
            monday_cb.setChecked(ride.getWeekDays().contains("1"));
            tuesday_cb.setChecked(ride.getWeekDays().contains("2"));
            wednesday_cb.setChecked(ride.getWeekDays().contains("3"));
            thursday_cb.setChecked(ride.getWeekDays().contains("4"));
            friday_cb.setChecked(ride.getWeekDays().contains("5"));
            saturday_cb.setChecked(ride.getWeekDays().contains("6"));
            sunday_cb.setChecked(ride.getWeekDays().contains("7"));
        }
    }

    @OnClick(R.id.neighborhood_et)
    public void neighborhoodEt() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                String selectedZone = getSelectedValue().toString();
                zone = selectedZone;
                if (selectedZone.equals("Outros")) {
                    showOtherNeighborhoodDialog();
                } else {
                    locationEt2(selectedZone);
                }
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.items(Util.getZones(), 0)
                .title(getContext().getString(R.string.frag_rideOffer_pickZone))
                .positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    public void showOtherNeighborhoodDialog() {
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                EditText neighborhood_et2 = (EditText) fragment.getDialog().findViewById(R.id.neighborhood_et);
                String neighborhood = neighborhood_et2.getText().toString();
                if (!neighborhood.isEmpty()) {
                    neighborhood_et.setText(neighborhood);
                }

                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.title(getActivity().getString(R.string.frag_ridesearch_typeNeighborhood))
                .positiveAction(getString(R.string.ok))
                .negativeAction(getString(R.string.cancel))
                .contentView(R.layout.other_neighborhood);

        DialogFragment fragment2 = DialogFragment.newInstance(builder);
        fragment2.show(getActivity().getSupportFragmentManager(), null);
    }

    public void locationEt2(String zone) {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                neighborhood_et.setText(getSelectedValue());
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.items(Util.getNeighborhoods(zone), 0)
                .title(getContext().getString(R.string.frag_rideOffer_pickNeighborhood))
                .positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.center_et)
    public void centerEt() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                center_et.setText(getSelectedValue());
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        if (going) {
            builder.items(Util.getCentersWithoutAllCenters(), 0)
                    .title(getContext().getString(R.string.frag_rideOffer_pickCenter))
                    .positiveAction(getContext().getString(R.string.ok))
                    .negativeAction(getContext().getString(R.string.cancel));
        } else {
            builder.items(Util.getHubs(), 0)
                    .title(getContext().getString(R.string.frag_rideOffer_pickHub))
                    .positiveAction(getContext().getString(R.string.ok))
                    .negativeAction(getContext().getString(R.string.cancel));
        }
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.date_et)
    public void date_et() {
        Dialog.Builder builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker_Light) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                date_et.setText(dialog.getFormattedDate(new SimpleDateFormat("dd/MM/yyyy", Locale.US)));
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.time_et)
    public void time_et() {
        Dialog.Builder builder = new TimePickerDialog.Builder(R.style.Material_App_Dialog_TimePicker_Light, 24, 0) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
                time_et.setText(dialog.getFormattedTime(new SimpleDateFormat("HH:mm", Locale.US)));
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.routine_cb)
    public void routineCb() {
        days_lo.setVisibility(routine_cb.isChecked() ? View.VISIBLE : View.GONE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, scrollView.getBottom());
            }
        });
    }

    @OnClick(R.id.send_bt)
    public void sendBt() {
        if (!checkCarOwnerDialog())
            return;

        String neighborhood = neighborhood_et.getText().toString();
        if (neighborhood.isEmpty()) {
            Util.toast(getString(R.string.frag_rideoffer_nullNeighborhood));
            return;
            /*neighborhood_et.setText(Util.getNeighborhoods(Util.getZones()[0])[0]);
            zone = Util.getZones()[0];
            neighborhood = neighborhood_et.getText().toString();*/
        }
        String place = place_et.getText().toString();
        String way = way_et.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String etDateString = date_et.getText().toString();
        Date todayDate = new Date();
        String todayString = simpleDateFormat.format(todayDate);
        try {
            todayDate = simpleDateFormat.parse(todayString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (etDateString.isEmpty()) {
            Util.toast(getString(R.string.frag_rideoffer_nullDate));
            return;
            /*date_et.setText(todayString);
            etDateString = todayString;*/
        } else {
            try {
                Date etDate = simpleDateFormat.parse(etDateString);
                if (etDate.before(todayDate)) {
                    Util.toast(getActivity().getString(R.string.frag_rideoffersearch_pastdate));
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String time = time_et.getText().toString();
        if (time.isEmpty()) {
            Util.toast(getString(R.string.frag_rideoffer_nullTime));
            return;
            /*SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm", Locale.US);
            String format = simpleDateFormat1.format(new Date());
            time_et.setText(format);
            time = format;*/
        }
        String slots = slots_et.getSelectedItemPosition() + 1 + "";
        String description = description_et.getText().toString();

        String hub = center_et.getText().toString();
        if (hub.isEmpty()) {
            if (going) {
                center_et.setText(Util.getCenters()[0]);
                hub = center_et.getText().toString();
            } else {
                center_et.setText(Util.getHubs()[0]);
                hub = center_et.getText().toString();
            }
        }

        boolean routine = routine_cb.isChecked();
        String weekDays = "", repeatsUntil = "";
        if (routine) {
            weekDays = monday_cb.isChecked() ? "1," : "";
            weekDays += tuesday_cb.isChecked() ? "2," : "";
            weekDays += wednesday_cb.isChecked() ? "3," : "";
            weekDays += thursday_cb.isChecked() ? "4," : "";
            weekDays += friday_cb.isChecked() ? "5," : "";
            weekDays += saturday_cb.isChecked() ? "6," : "";
            weekDays += sunday_cb.isChecked() ? "7," : "";

            if (weekDays.isEmpty()) {
                Util.toast(R.string.frag_rideOffer_noRoutineDays);
                return;
            }
            weekDays = weekDays.substring(0, weekDays.length() - 1);

            int months = 0;
            int id2 = radioGroup2.getCheckedRadioButtonId();
            switch (id2) {
                case R.id.r2months_rb:
                    months = 2;
                    break;
                case R.id.r3months_rb:
                    months = 3;
                    break;
                case R.id.r4months_rb:
                    months = 4;
                    break;
            }

            Calendar c = Calendar.getInstance();
            try {
                c.setTime(simpleDateFormat.parse(etDateString));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.add(Calendar.MONTH, months);
            repeatsUntil = simpleDateFormat.format(c.getTime());
        }

        final Ride ride = new Ride(zone, neighborhood, place, way, etDateString, time, slots, hub, description, going, routine, weekDays, repeatsUntil);

        List<Ride> rides3 = Ride.find(Ride.class, "date = ? and going = ?", etDateString, going ? "1" : "0");
        if (rides3 != null && !rides3.isEmpty()) {
            Util.toast(getString(R.string.frag_rideoofer_rieOfferConflict));
            return;
        }

        String lastRideOffer = new Gson().toJson(ride);
        if (going)
            SharedPref.saveLastRideGoingPref(lastRideOffer);
        else
            SharedPref.saveLastRideNotGoingPref(lastRideOffer);

        final ProgressDialog pd = ProgressDialog.show(getContext(), "", getString(R.string.wait), true, true);
        App.getNetworkService(getContext()).offerRide(Util.getHeaderForHttp(getContext()), ride)
                .enqueue(new Callback<List<RideRountine>>() {
                    @Override
                    public void onResponse(Call<List<RideRountine>> call, Response<List<RideRountine>> response) {
                        if (response.isSuccessful()) {

//                            List<Ride> rides = response.body();
                            List<RideRountine> rideRountines = response.body();
                            List<Ride> rides = new ArrayList<Ride>();
                            for (RideRountine rideRountine : rideRountines){
                                rides.add(new Ride(rideRountine));
                            }

                            for (Ride ride : rides) {
                                Ride ride2 = new Ride(ride);
                                ride2.setDbId(ride.getId().intValue());
                                FirebaseTopicsHandler.subscribeFirebaseTopic(String.valueOf(ride.getId().intValue()));
                                ride2.save();
                                createChatAssets(ride2);
                            }
                            pd.dismiss();
                            Util.toast(R.string.frag_rideOffer_rideSaved);
                        } else {
                            Util.treatResponseFromServer(response);
                            pd.dismiss();
                            if (response.code() == 403){
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
                        if (t.getMessage().equals(ERROR_MESSAGE)){
                            Util.toast(R.string.frag_rideOffer_ridesCreated);
                        } else {
                            Util.toast(R.string.frag_rideOffer_errorRideSaved);
                            Log.e("offerRide", t.getMessage());
                        }
                    }
                });
    }

    private void createChatAssets(Ride ride) {
        Ride rideWithUsers = ride;

        Context context = getContext();

        int color = 0, bgRes = 0;
        if (rideWithUsers.getZone().equals("Centro")) {
            color = ContextCompat.getColor(context, R.color.zone_centro);
            bgRes = R.drawable.bg_bt_raise_zone_centro;
        }
        if (rideWithUsers.getZone().equals("Zona Sul")) {
            color = ContextCompat.getColor(context, R.color.zone_sul);
            bgRes = R.drawable.bg_bt_raise_zone_sul;
        }
        if (rideWithUsers.getZone().equals("Zona Oeste")) {
            color = ContextCompat.getColor(context, R.color.zone_oeste);
            bgRes = R.drawable.bg_bt_raise_zone_oeste;
        }
        if (rideWithUsers.getZone().equals("Zona Norte")) {
            color = ContextCompat.getColor(context, R.color.zone_norte);
            bgRes = R.drawable.bg_bt_raise_zone_norte;
        }
        if (rideWithUsers.getZone().equals("Baixada")) {
            color = ContextCompat.getColor(context, R.color.zone_baixada);
            bgRes = R.drawable.bg_bt_raise_zone_baixada;
        }
        if (rideWithUsers.getZone().equals("Grande Niterói")) {
            color = ContextCompat.getColor(context, R.color.zone_niteroi);
            bgRes = R.drawable.bg_bt_raise_zone_niteroi;
        }
        if (rideWithUsers.getZone().equals("Outros")) {
            color = ContextCompat.getColor(context, R.color.zone_outros);
            bgRes = R.drawable.bg_bt_raise_zone_outros;
        }

        final String location;
        if (rideWithUsers.isGoing())
            location = rideWithUsers.getNeighborhood() + " ➜ " + rideWithUsers.getHub();
        else
            location = rideWithUsers.getHub() + " ➜ " + rideWithUsers.getNeighborhood();

        final int finalColor = color, finalBgRes = bgRes;

        List<ChatAssets> l = ChatAssets.find(ChatAssets.class, "ride_id = ?", rideWithUsers.getDbId() + "");
        if (l == null || l.isEmpty())
            new ChatAssets(rideWithUsers.getDbId() + "", location, finalColor, finalBgRes,
                    Util.formatBadDateWithoutYear(rideWithUsers.getDate()),
                    Util.formatTime(rideWithUsers.getTime())).save();
    }
}
