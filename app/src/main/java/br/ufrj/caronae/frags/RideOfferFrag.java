package br.ufrj.caronae.frags;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.models.ModelValidateDuplicate;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideRountine;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideOfferFrag extends Fragment {

    @BindView(R.id.radioGroup2)
    RadioGroup radioGroup2;

    @BindView(R.id.neighborhood_et)
    EditText neighborhood_et;
    @BindView(R.id.place_et)
    EditText place_et;
    @BindView(R.id.way_et)
    EditText way_et;
    @BindView(R.id.date_et)
    TextView date_et;
    @BindView(R.id.time_et)
    TextView time_et;
    @BindView(R.id.slots_et)
    Spinner slots_et;
    @BindView(R.id.center_et)
    EditText center_et;
    @BindView(R.id.campi_et)
    EditText campi_et;
    @BindView(R.id.description_et)
    EditText description_et;

    @BindView(R.id.routine_cb)
    CheckBox routine_cb;
    @BindView(R.id.days_lo)
    RelativeLayout days_lo;

    @BindView(R.id.monday_cb)
    CheckBox monday_cb;
    @BindView(R.id.tuesday_cb)
    CheckBox tuesday_cb;
    @BindView(R.id.wednesday_cb)
    CheckBox wednesday_cb;
    @BindView(R.id.thursday_cb)
    CheckBox thursday_cb;
    @BindView(R.id.friday_cb)
    CheckBox friday_cb;
    @BindView(R.id.saturday_cb)
    CheckBox saturday_cb;
    @BindView(R.id.sunday_cb)
    CheckBox sunday_cb;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private String zone;
    private boolean going;
    ProgressDialog pd;

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

    @OnClick(R.id.campi_et)
    public void campiEt() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                campi_et.setText(getSelectedValue());
                if ((campi_et.getText().toString().equals("") || campi_et.getText().toString().equals(Util.getCampi()[2]) && (going))) {
//                    center_et.setVisibility(View.GONE);
                } else
                    center_et.setVisibility(View.VISIBLE);
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.items(Util.getCampiWithoutAllCampi(), 0)
                .title(getContext().getString(R.string.frag_rideOffer_pickCampi))
                .positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.center_et)
    public void centerEt() {
        final SimpleDialog.Builder centerBuilder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                center_et.setText(getSelectedValue());
                super.onPositiveActionClicked(fragment);
            }
        };

        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                if (going) {
                    if (getSelectedValue().toString().equals("Praia Vermelha")) {
                        center_et.setText(getSelectedValue());
                    } else {
                        centerBuilder.items(Util.getCentersByCampi(getSelectedValue().toString()), 0)
                                .title(getContext().getString(R.string.frag_rideOffer_pickCenter))
                                .positiveAction(getContext().getString(R.string.ok))
                                .negativeAction(getContext().getString(R.string.cancel));
                        DialogFragment centerFragment = DialogFragment.newInstance(centerBuilder);
                        centerFragment.show(getFragmentManager(), null);
                    }
                } else {
                    centerBuilder.items(Util.getHubsByCampi(getSelectedValue().toString()), 0)
                            .title(getContext().getString(R.string.frag_rideOffer_pickHub))
                            .positiveAction(getContext().getString(R.string.ok))
                            .negativeAction(getContext().getString(R.string.cancel));
                    DialogFragment centerFragment = DialogFragment.newInstance(centerBuilder);
                    centerFragment.show(getFragmentManager(), null);
                }
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };


        if (going) {
            builder.items(Util.getCampiWithoutAllCampi(), 0)
                    .title(getContext().getString(R.string.frag_rideOffer_pickCenter))
                    .positiveAction(getContext().getString(R.string.ok))
                    .negativeAction(getContext().getString(R.string.cancel));
            DialogFragment fragment = DialogFragment.newInstance(builder);
            fragment.show(getFragmentManager(), null);
        } else {
            builder.items(Util.getCampiWithoutAllCampi(), 0)
                    .title(getContext().getString(R.string.frag_rideOffer_pickHub))
                    .positiveAction(getContext().getString(R.string.ok))
                    .negativeAction(getContext().getString(R.string.cancel));
            DialogFragment fragment = DialogFragment.newInstance(builder);
            fragment.show(getFragmentManager(), null);


//        if (going) {
//            builder.items(Util.getCentersWithoutAllCenters(), 0)
//                    .title(getContext().getString(R.string.frag_rideOffer_pickCenter))
//                    .positiveAction(getContext().getString(R.string.ok))
//                    .negativeAction(getContext().getString(R.string.cancel));
//            DialogFragment fragment = DialogFragment.newInstance(builder);
//            fragment.show(getFragmentManager(), null);
//        } else {
//            builder.items(Util.getFundaoHubs(), 0)
//                    .title(getContext().getString(R.string.frag_rideOffer_pickHub))
//                    .positiveAction(getContext().getString(R.string.ok))
//                    .negativeAction(getContext().getString(R.string.cancel));
//            DialogFragment fragment = DialogFragment.newInstance(builder);
//            fragment.show(getFragmentManager(), null);
//            if (campi_et.getText().toString().equals("")){
//                campi_et.setError("Escolher o campus");
//            } else {
//                if (campi_et.getText().toString().equals(Util.getCampus()[1])) {
//                    builder.items(Util.getFundaoHubs(), 0);
//                }
//                if (campi_et.getText().toString().equals(Util.getCampus()[2])) {
//                    builder.items(Util.getPraiaVermelhaHubs(), 0);
//                }
//                DialogFragment fragment = DialogFragment.newInstance(builder);
//                fragment.show(getFragmentManager(), null);
//            }
        }
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
        }
        String slots = slots_et.getSelectedItemPosition() + 1 + "";
        String description = description_et.getText().toString();

        String hub = center_et.getText().toString();
        if (hub.isEmpty()) {
            if (going) {
                center_et.setText(Util.getFundaoCenters()[0]);
                hub = center_et.getText().toString();
            } else {
                center_et.setText(Util.getFundaoHubs()[0]);
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

        String campus = campi_et.getText().toString();

        final Ride ride = new Ride(zone, neighborhood, place, way, etDateString, time, slots, hub, campus, description, going, routine, weekDays, repeatsUntil);


        checkAndCreateRide(ride);

        String lastRideOffer = new Gson().toJson(ride);
        if (going)
            SharedPref.saveLastRideGoingPref(lastRideOffer);
        else
            SharedPref.saveLastRideNotGoingPref(lastRideOffer);
    }

    private void createChatAssets(Ride ride) {
        Util.createChatAssets(ride, getContext());
    }

    private void checkAndCreateRide(final Ride ride) {
        pd = ProgressDialog.show(getContext(), "", getString(R.string.wait), true, true);
        App.getNetworkService(getContext()).validateDuplicates(ride.getDate(), ride.getTime() + ":00", ride.isGoing() ? 1 : 0)
                .enqueue(new Callback<ModelValidateDuplicate>() {
                    @Override
                    public void onResponse(Call<ModelValidateDuplicate> call, Response<ModelValidateDuplicate> response) {
                        if (response.isSuccessful()) {
                            ModelValidateDuplicate validateDuplicate = response.body();
                            if (validateDuplicate.isValid()) {
                                createRide(ride);
                            } else {
                                if (validateDuplicate.getStatus().equals("possible_duplicate")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setView(R.layout.possible_duplicate_rides_dialog);
                                    builder.setPositiveButton("Criar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            createRide(ride);
                                        }
                                    });
                                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setView(R.layout.duplicate_rides_dialog);
                                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                }
                            }
                        } else {
                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelValidateDuplicate> call, Throwable t) {
                        pd.dismiss();
                    }
                });
    }

    private void createRide(Ride ride) {
        App.getNetworkService(getContext()).offerRide(ride)
                .enqueue(new Callback<List<RideRountine>>() {
                    @Override
                    public void onResponse(Call<List<RideRountine>> call, Response<List<RideRountine>> response) {
                        if (response.isSuccessful()) {

                            List<RideRountine> rideRountines = response.body();
                            List<Ride> rides = new ArrayList<>();
                            for (RideRountine rideRountine : rideRountines) {
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
