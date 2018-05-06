package br.ufrj.caronae.frags;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.CustomDateTimePicker;
import br.ufrj.caronae.CustomDialogClass;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.httpapis.CaronaeAPI;
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

    @BindView(R.id.tab1)
    RelativeLayout isGoing_bt;
    @BindView(R.id.tab2)
    RelativeLayout isLeaving_bt;
    @BindView(R.id.days_lo)
    RelativeLayout days_lo;

    @BindView(R.id.tab1_tv)
    TextView isGoing_tv;
    @BindView(R.id.tab2_tv)
    TextView isLeaving_tv;
    @BindView(R.id.time_et)
    public TextView time_et;

    @BindView(R.id.radioGroup2)
    RadioGroup radioGroup2;

    @BindView(R.id.neighborhood_et)
    EditText neighborhood_et;
    @BindView(R.id.place_et)
    EditText place_et;
    @BindView(R.id.way_et)
    EditText way_et;
    @BindView(R.id.center_et)
    EditText center_et;
    @BindView(R.id.description_et)
    EditText description_et;

    @BindView(R.id.routine_cb)
    SwitchCompat routine_cb;

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
    public String time;
    ProgressDialog pd;
    public Ride ride;

    public RideOfferFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_ride_offer, container, false);
        ButterKnife.bind(this, view);

        going = true;
        setButton(isLeaving_bt, isGoing_bt,isLeaving_tv, isGoing_tv);

        if(SharedPref.getGoingLabel() != null)
        {
            isGoing_tv.setText(SharedPref.getGoingLabel());
        }
        if(SharedPref.getLeavingLabel() != null)
        {
            isLeaving_tv.setText(SharedPref.getLeavingLabel());
        }

        setInitialDate();

        String[] items = new String[6];
        for (int i = 0; i < items.length; i++) {
            items[i] = String.valueOf(i + 1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.row_spn, items);
        adapter.setDropDownViewResource(R.layout.row_spn_dropdown);

        neighborhood_et.setText(R.string.frag_rideoffer_neighborHint);
        center_et.setText(going ?R.string.frag_ridesearch_campiHint : R.string.frag_rideOffer_hintPickHub);

        String lastRideOffer = going ? SharedPref.getLastRideGoingPref() : SharedPref.getLastRideNotGoingPref();
        if (!lastRideOffer.equals(SharedPref.MISSING_PREF)) {
            loadLastRide(lastRideOffer);
        }
        if(going) {
            checkCarOwnerDialog();
        }

        return view;
    }

    private boolean checkCarOwnerDialog() {
        Activity act = getActivity();
        Fragment frag = this;
        if (!App.getUser().isCarOwner()) {
            CustomDialogClass cdc = new CustomDialogClass(act,"ROFINCO", frag);
            cdc.show();
            cdc.setTitleText("Você possui carro?");
            cdc.setMessageText( "Parece que você marcou no seu perfil que não possui um carro. Para criar uma carona, preencha os dados do seu carro no seu perfil.");
            cdc.setPButtonText("OK");
            cdc.enableOnePositiveOption();
            return false;
        }
        return true;
    }

    private void loadLastRide(String lastRideOffer) {
        ride = new Gson().fromJson(lastRideOffer, Ride.class);
        zone = ride.getZone();
        neighborhood_et.setText(ride.getNeighborhood());
        place_et.setText(ride.getPlace());
        way_et.setText(ride.getRoute());
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

    @Override
    public void onStart()
    {
        Util.debug(SharedPref.LOCATION_INFO);
        if(!SharedPref.LOCATION_INFO.isEmpty() && !SharedPref.LOCATION_INFO.equals(""))
        {
            neighborhood_et.setText(SharedPref.LOCATION_INFO);
            SharedPref.LOCATION_INFO = "";
        }
        if(!SharedPref.CAMPI_INFO.isEmpty() && !SharedPref.CAMPI_INFO.equals(""))
        {
            center_et.setText(SharedPref.CAMPI_INFO);
            SharedPref.CAMPI_INFO = "";
        }
        super.onStart();
    }

    @Override
    public void onResume()
    {
        if(!SharedPref.LOCATION_INFO.isEmpty() && !SharedPref.LOCATION_INFO.equals(""))
        {
            neighborhood_et.setText(SharedPref.LOCATION_INFO);
            SharedPref.LOCATION_INFO = "";
        }
        if(!SharedPref.CAMPI_INFO.isEmpty() && !SharedPref.CAMPI_INFO.equals(""))
        {
            center_et.setText(SharedPref.CAMPI_INFO);
            SharedPref.CAMPI_INFO = "";
        }
        super.onResume();
    }

    @OnClick(R.id.neighborhood_et)
    public void neighborhoodEt() {
        Intent intent = new Intent(getActivity(), PlaceAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("backText", "Criar");
        intent.putExtra("selection", "neigh");
        intent.putExtra("allP", false);
        intent.putExtra("otherP", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.center_et)
    public void centerEt() {
        Intent intent = new Intent(getActivity(), PlaceAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("backText", "Criar");
        if(going) {
            intent.putExtra("selection", "center");
        }
        else
        {
            intent.putExtra("selection", "hub");
        }
        intent.putExtra("allP", false);
        intent.putExtra("otherP", false);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.time_et)
    public void time_et() {
        Activity activity = getActivity();
        CustomDateTimePicker cdtp;
        if(going) {
            if(SharedPref.getGoingLabel() == null)
                cdtp = new CustomDateTimePicker(activity, getResources().getString(R.string.go_rb), time, this, "Offer");
            else
                cdtp = new CustomDateTimePicker(activity, SharedPref.getGoingLabel(), time, this, "Offer");
        }else
        {
            if(SharedPref.getLeavingLabel() == null)
                cdtp = new CustomDateTimePicker(activity, getResources().getString(R.string.back_rb), time, this, "Offer");
            else
                cdtp = new CustomDateTimePicker(activity, SharedPref.getLeavingLabel(), time, this, "Offer");
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
        Activity act = getActivity();
        Fragment frag = this;
        if (!checkCarOwnerDialog()) {
            return;
        }
        String neighborhood = neighborhood_et.getText().toString();
        String hub = center_et.getText().toString();
        if(hub.isEmpty() || neighborhood.isEmpty())
        {
            CustomDialogClass cdc = new CustomDialogClass(act,"ROFD", frag);
            cdc.show();
            cdc.setTitleText("Dados incompletos");
            cdc.setMessageText( "Ops! Parece que você esqueceu de preencher o local da sua carona.");
            cdc.setPButtonText("OK");
            cdc.enableOnePositiveOption();
            return;
        }
        String place = place_et.getText().toString();
        String way = way_et.getText().toString();
        String time = time_et.getText().toString();
        String date = time_et.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String description = description_et.getText().toString();

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
                CustomDialogClass cdc = new CustomDialogClass(act,"ROFD", frag);
                cdc.show();
                cdc.setTitleText("Dados incompletos");
                cdc.setMessageText("Ops! Parece que você esqueceu de marcar os dias da rotina.");
                cdc.setPButtonText("OK");
                cdc.enableOnePositiveOption();
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
                c.setTime(simpleDateFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.add(Calendar.MONTH, months);
            repeatsUntil = simpleDateFormat.format(c.getTime());
        }

        ride = new Ride(zone, neighborhood, place, way, date, time, "", hub, "", description, going, routine, weekDays, repeatsUntil);

        checkAndCreateRide();

        String lastRideOffer = new Gson().toJson(ride);
        if (going)
            SharedPref.saveLastRideGoingPref(lastRideOffer);
        else
            SharedPref.saveLastRideNotGoingPref(lastRideOffer);
    }

    private void createChatAssets(Ride ride) {
        Util.createChatAssets(ride, getContext());
    }

    private void checkAndCreateRide() {
        Activity act = getActivity();
        Fragment frag = this;
        pd = ProgressDialog.show(getContext(), "", getString(R.string.wait), true, true);
        CaronaeAPI.service(getContext()).validateDuplicates(ride.getDate(), ride.getTime() + ":00", ride.isGoing() ? 1 : 0)
                .enqueue(new Callback<ModelValidateDuplicate>() {
                    @Override
                    public void onResponse(Call<ModelValidateDuplicate> call, Response<ModelValidateDuplicate> response) {
                        if (response.isSuccessful()) {
                            ModelValidateDuplicate validateDuplicate = response.body();
                            if (validateDuplicate.isValid()) {
                                createRide();
                                changeFragment();
                            } else {
                                CustomDialogClass cdc;
                                if (validateDuplicate.getStatus().equals("possible_duplicate")) {
                                    cdc = new CustomDialogClass(act,"ROFPD", frag);
                                    cdc.show();
                                    cdc.setTitleText( "Parece que você já ofereceu uma carona para este dia");
                                    cdc.setMessageText("Você pode cancelar e verificar as suas caronas ou continuar e criar a carona mesmo assim.");
                                    cdc.setNButtonText("Criar");
                                    cdc.setPButtonText("Cancelar");
                                    cdc.setNegativeButtonColor(R.color.darkblue2);

                                } else{
                                    cdc = new CustomDialogClass(act,"ROFD", frag);
                                    cdc.show();
                                    cdc.setTitleText( "Você já ofereceu uma carona muito parecida com essa");
                                    cdc.setMessageText("Você pode verificar as suas caronas na seção 'Minhas' do aplicativo.");
                                    cdc.setPButtonText("OK");
                                    cdc.enableOnePositiveOption();
                                }
                            }
                        } else {
                            CustomDialogClass cdc = new CustomDialogClass(act,"ROFD", frag);;
                            cdc.show();
                            cdc.setTitleText( "Não foi possível validar sua carona");
                            cdc.setMessageText("Houve um erro de comunicação com nosso servidor. Por favor, tente novamente.");
                            cdc.setPButtonText("OK");
                            cdc.enableOnePositiveOption();
                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelValidateDuplicate> call, Throwable t) {
                        CustomDialogClass cdc = new CustomDialogClass(act,"ROFD", frag);;
                        cdc.show();
                        cdc.setTitleText( "Não foi possível validar sua carona");
                        cdc.setMessageText("Houve um erro de comunicação com nosso servidor. Por favor, tente novamente."+t.getLocalizedMessage());
                        cdc.setPButtonText("OK");
                        cdc.enableOnePositiveOption();
                        pd.dismiss();
                    }
                });
    }

    public void createRide() {
        CaronaeAPI.service(getContext()).offerRide(ride)
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

    public void changeFragment()
    {
        Fragment fragment = null;
        try {
            fragment = (Fragment) MyRidesFrag.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
        transaction.replace(R.id.flContent, fragment).commit();
        SharedPref.NAV_INDICATOR = "MyRides";
    }

    private void setInitialDate()
    {
        Calendar rightNow = Calendar.getInstance();
        Date date = rightNow.getTime();
        SimpleDateFormat dateWithYear = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String ddmmyyyy = dateWithYear.format(date);
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

        time_et.setText(time);
    }

    private void setHint()
    {
        center_et.setText("");
        if(going)
        {
            center_et.setHint("Centro Universitário");
        }
        else
        {
            center_et.setHint("Escolha o hub de encontro");
        }
    }

    @OnClick(R.id.tab1)
    public void goingTabSelected()
    {
        if(!going)
        {
            going = true;
            setHint();
            setButton(isLeaving_bt, isGoing_bt,isLeaving_tv, isGoing_tv);
        }
    }

    @OnClick(R.id.tab2)
    public void leavingTabSelected()
    {
        if(going)
        {
            going = false;
            setHint();
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
}
