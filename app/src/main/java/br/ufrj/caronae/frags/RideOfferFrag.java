package br.ufrj.caronae.frags;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.customizedviews.CustomDateTimePicker;
import br.ufrj.caronae.customizedviews.CustomDialogClass;
import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.ModelValidateDuplicate;
import br.ufrj.caronae.models.RideOffer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
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
    @BindView(R.id.monday_cb)
    RelativeLayout monday_cb;
    @BindView(R.id.tuesday_cb)
    RelativeLayout tuesday_cb;
    @BindView(R.id.wednesday_cb)
    RelativeLayout wednesday_cb;
    @BindView(R.id.thursday_cb)
    RelativeLayout thursday_cb;
    @BindView(R.id.friday_cb)
    RelativeLayout friday_cb;
    @BindView(R.id.saturday_cb)
    RelativeLayout saturday_cb;
    @BindView(R.id.sunday_cb)
    RelativeLayout sunday_cb;
    @BindView(R.id.add_slot)
    RelativeLayout addSlotButton;
    @BindView(R.id.remove_slot)
    RelativeLayout removeSlotButton;

    @BindView(R.id.tab1_tv)
    TextView isGoing_tv;
    @BindView(R.id.tab2_tv)
    TextView isLeaving_tv;
    @BindView(R.id.mon_tv)
    TextView mon_tv;
    @BindView(R.id.tue_tv)
    TextView tue_tv;
    @BindView(R.id.wed_tv)
    TextView wed_tv;
    @BindView(R.id.thu_tv)
    TextView thu_tv;
    @BindView(R.id.fri_tv)
    TextView fri_tv;
    @BindView(R.id.sat_tv)
    TextView sat_tv;
    @BindView(R.id.sun_tv)
    TextView sun_tv;
    @BindView(R.id.time_et)
    public TextView time_et;

    @BindView(R.id.slots_n)
    TextView slotNumber;
    @BindView(R.id.remove_tv)
    TextView remove_tv;
    @BindView(R.id.add_tv)
    TextView add_tv;

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

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private int slots;
    private boolean going;
    private boolean[] checked;
    public String time;
    ProgressDialog pd;
    public RideOffer ride;

    public RideOfferFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_ride_offer, container, false);
        ButterKnife.bind(this, view);

        checked = new boolean[7];
        slots = 1;
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

        String lastRideOffer = going ? SharedPref.getLastRideGoingPref() : SharedPref.getLastRideNotGoingPref();
        if (!lastRideOffer.equals(SharedPref.MISSING_PREF)) {
            loadLastRide(lastRideOffer);
        }
        else
        {
            neighborhood_et.setText(R.string.neighborhood);
            center_et.setText(going ? R.string.fragment_ridesearch_campi_hint : R.string.fragment_rideoffer_hub_hint);
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
        ride = new Gson().fromJson(lastRideOffer, RideOffer.class);
        neighborhood_et.setText(ride.getNeighborhood());
        place_et.setText(ride.getPlace());
        way_et.setText(ride.getRoute());
        center_et.setText(ride.getHub());
        description_et.setText(ride.getDescription());
        routine_cb.setChecked(true);
        for(int i = 0; i < 7; i++) {
            setChecked(i);
        }
    }

    @Override
    public void onStart()
    {
        try {
            ((MainAct) getActivity()).setCheckedItem();
        }
        catch (Exception e)
        {

        }
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
        try {
            ((MainAct) getActivity()).setCheckedItem();
        }
        catch (Exception e)
        {

        }
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
                cdtp = new CustomDateTimePicker(activity, getResources().getString(R.string.arriving_ufrj), time, this, "Offer");
            else
                cdtp = new CustomDateTimePicker(activity, SharedPref.getGoingLabel(), time, this, "Offer");
        }else
        {
            if(SharedPref.getLeavingLabel() == null)
                cdtp = new CustomDateTimePicker(activity, getResources().getString(R.string.leaving_ufrj), time, this, "Offer");
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
        String hubCenter = center_et.getText().toString();
        String zone = Util.whichZone(neighborhood);
        if(hubCenter.isEmpty() || neighborhood.isEmpty() || hubCenter.equals("Centro Universitário") || hubCenter.equals("Escolha o hub de encontro") || neighborhood.equals("Bairro"))
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
        //39/19/9999 24:69
        //0123456789012345
        String time = time_et.getText().toString().substring(11)+":00";
        String date = time_et.getText().toString().substring(0,10);
        String description = description_et.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date currentDate = new Date(System.currentTimeMillis()+5*60*1000);
        String getCurrentDateTime = simpleDateFormat.format(currentDate);
        String dateToCompare = date + " " + time.substring(0,time.length()-3);
        Date time1 = new Date(), time2 = new Date();
        try
        {
            time1 = simpleDateFormat.parse(getCurrentDateTime);
            time2 = simpleDateFormat.parse(dateToCompare);
        }
        catch (Exception e)
        {
            Util.debug("Error while setting date");
        }

        if (time1.after(time2))
        {
            CustomDialogClass cdc = new CustomDialogClass(act,"ROFD", frag);
            cdc.show();
            cdc.setTitleText( "Não foi possível validar sua carona");
            cdc.setMessageText("Houve um erro ao criar sua carona. Não é possível criar caronas no passado ou com menos de 5 minutos de antecedência. Por favor, selecione outra data e tente novamente.");
            cdc.setPButtonText("OK");
            cdc.enableOnePositiveOption();
            return;
        }

        boolean routine = routine_cb.isChecked();
        String weekDays = "", repeatsUntil = "";

        if (routine) {
            for(int i = 0; i < 7; i++)
            {
                weekDays = weekDays.concat(checked[i] ? (i+1)+"," : "");
            }

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
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date d = format.parse(date);
                c.setTime(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.add(Calendar.MONTH, months);
            repeatsUntil = Util.formatBadDateWithYear(simpleDateFormat.format(c.getTime()));
        }

        ride = new RideOffer(time, neighborhood, repeatsUntil, description, place, going, date, 0, slots, zone, weekDays, hubCenter, way);

        checkAndCreateRide();

        String lastRideOffer = new Gson().toJson(ride);
        if (going) {
            SharedPref.saveLastRideGoingPref(lastRideOffer);
        }
        else {
            SharedPref.saveLastRideNotGoingPref(lastRideOffer);
        }
    }

    private void checkAndCreateRide() {
        Activity act = getActivity();
        Fragment frag = this;
        pd = ProgressDialog.show(getContext(), "", getString(R.string.wait), true, true);
        CaronaeAPI.service().validateDuplicates(ride.getDate(), ride.getTime(), ride.isGoing() ? 1 : 0)
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
                                    cdc.setNegativeButtonColor(getResources().getColor(R.color.darkblue));

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
                        cdc.setMessageText("Houve um erro de comunicação com nosso servidor. Por favor, tente novamente. "+t.getLocalizedMessage());
                        cdc.setPButtonText("OK");
                        cdc.enableOnePositiveOption();
                        pd.dismiss();
                    }
                });
    }

    public void createRide() {
        final Activity activity = getActivity();
        final Fragment fragment = this;
        CaronaeAPI.service().offerRide(ride)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            pd.dismiss();
                            SharedPref.lastAllRidesUpdate = null;
                            SharedPref.lastMyRidesUpdate = 350;
                            changeFragment();
                        } else {
                            Util.treatResponseFromServer(response);
                            pd.dismiss();
                            CustomDialogClass cdc = new CustomDialogClass(activity,"ROFD", fragment);;
                            cdc.show();
                            if (response.code() == 403) {
                                cdc.setTitleText( "Não foi possível validar sua carona");
                                cdc.setMessageText("Houve um erro ao criar sua carona. Não é possível criar caronas no passado. Por favor, tente novamente.");
                                cdc.setPButtonText("OK");
                                cdc.enableOnePositiveOption();
                            } else {
                                cdc.show();
                                cdc.setTitleText( "Não foi possível validar sua carona");
                                cdc.setMessageText("Houve um erro de comunicação com nosso servidor. Por favor, tente novamente." + response.message());
                                cdc.setPButtonText("OK");
                                Log.e("error " , response.message());
                                cdc.enableOnePositiveOption();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        CustomDialogClass cdc = new CustomDialogClass(activity,"ROFD", fragment);;
                        cdc.show();
                        cdc.setTitleText( "Não foi possível validar sua carona");
                        cdc.setMessageText("Houve um erro de comunicação com nosso servidor. Por favor, tente novamente. "+t.getLocalizedMessage());
                        cdc.setPButtonText("OK");
                        cdc.enableOnePositiveOption();
                        Log.e("error " , t.getMessage());
                        pd.dismiss();
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
        ((MainAct)getActivity()).navigation.getMenu().getItem(1).setChecked(true);
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
        String lastRideOffer = going ? SharedPref.getLastRideGoingPref() : SharedPref.getLastRideNotGoingPref();

        if (!lastRideOffer.equals(SharedPref.MISSING_PREF)) {
            ride = new Gson().fromJson(lastRideOffer, RideOffer.class);
            center_et.setText(ride.getHub());
        }
        else
        {
            String newRide = going ? "Centro Universitário" : "Escolha o hub de encontro";
            center_et.setText(newRide);
        }
    }

    @OnClick(R.id.monday_cb)
    public void monClick()
    {
        isChecked(0);
    }

    @OnClick(R.id.tuesday_cb)
    public void tueClick()
    {
        isChecked(1);
    }

    @OnClick(R.id.wednesday_cb)
    public void wedClick()
    {
        isChecked(2);
    }

    @OnClick(R.id.thursday_cb)
    public void thuClick()
    {
        isChecked(3);
    }

    @OnClick(R.id.friday_cb)
    public void friClick()
    {
        isChecked(4);
    }

    @OnClick(R.id.saturday_cb)
    public void satClick()
    {
        isChecked(5);
    }

    @OnClick(R.id.sunday_cb)
    public void sunClick()
    {
        isChecked(6);
    }

    @OnClick(R.id.remove_slot)
    public void removeSlotClick()
    {
        slots -= 1;
        updateSlots();
    }

    @OnClick(R.id.add_slot)
    public void addSlotClick()
    {
        slots += 1;
        updateSlots();
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

    private void isChecked(int pos)
    {
        if(checked[pos])
        {
            checked[pos] = false;
        }
        else
        {
            checked[pos] = true;
        }
        setChecked(pos);
    }

    private void setChecked(int pos)
    {
        GradientDrawable layoutShape = new GradientDrawable();
        switch (pos)
        {
            case 0:
                layoutShape = (GradientDrawable)monday_cb.getBackground();
                if(checked[pos])
                {
                    mon_tv.setTextColor(getResources().getColor(R.color.white));
                }
                else
                {
                    mon_tv.setTextColor(getResources().getColor(R.color.dark_gray));
                }
                break;
            case 1:
                layoutShape = (GradientDrawable)tuesday_cb.getBackground();
                if(checked[pos])
                {
                    tue_tv.setTextColor(getResources().getColor(R.color.white));
                }
                else
                {
                    tue_tv.setTextColor(getResources().getColor(R.color.dark_gray));
                }
                break;
            case 2:
                layoutShape = (GradientDrawable)wednesday_cb.getBackground();
                if(checked[pos])
                {
                    wed_tv.setTextColor(getResources().getColor(R.color.white));
                }
                else
                {
                    wed_tv.setTextColor(getResources().getColor(R.color.dark_gray));
                }
                break;
            case 3:
                layoutShape = (GradientDrawable)thursday_cb.getBackground();
                if(checked[pos])
                {
                    thu_tv.setTextColor(getResources().getColor(R.color.white));
                }
                else
                {
                    thu_tv.setTextColor(getResources().getColor(R.color.dark_gray));
                }
                break;
            case 4:
                layoutShape = (GradientDrawable)friday_cb.getBackground();
                if(checked[pos])
                {
                    fri_tv.setTextColor(getResources().getColor(R.color.white));
                }
                else
                {
                    fri_tv.setTextColor(getResources().getColor(R.color.dark_gray));
                }
                break;
            case 5:
                layoutShape = (GradientDrawable)saturday_cb.getBackground();
                if(checked[pos])
                {
                    sat_tv.setTextColor(getResources().getColor(R.color.white));
                }
                else
                {
                    sat_tv.setTextColor(getResources().getColor(R.color.dark_gray));
                }
                break;
            case 6:
                layoutShape = (GradientDrawable)sunday_cb.getBackground();
                if(checked[pos])
                {
                    sun_tv.setTextColor(getResources().getColor(R.color.white));
                }
                else
                {
                    sun_tv.setTextColor(getResources().getColor(R.color.dark_gray));
                }
                break;
        }

        if(checked[pos])
        {
            layoutShape.setColor(getResources().getColor(R.color.dark_gray));
        }
        else
        {
            layoutShape.setColor(getResources().getColor(R.color.white));
        }
    }

    private void updateSlots()
    {
        if(slots <= 1)
        {
            removeSlotButton.setFocusable(false);
            removeSlotButton.setClickable(false);
            addSlotButton.setFocusable(true);
            addSlotButton.setClickable(true);
            remove_tv.setTextColor(getResources().getColor(R.color.gray));
            add_tv.setTextColor(getResources().getColor(R.color.black));
        }
        else if(slots >= 6)
        {
            addSlotButton.setFocusable(false);
            addSlotButton.setClickable(false);
            removeSlotButton.setFocusable(true);
            removeSlotButton.setClickable(true);
            remove_tv.setTextColor(getResources().getColor(R.color.black));
            add_tv.setTextColor(getResources().getColor(R.color.gray));
        }
        else
        {
            removeSlotButton.setFocusable(true);
            removeSlotButton.setClickable(true);
            addSlotButton.setFocusable(true);
            addSlotButton.setClickable(true);
            remove_tv.setTextColor(getResources().getColor(R.color.black));
            add_tv.setTextColor(getResources().getColor(R.color.black));
        }
        slotNumber.setText(Integer.toString(slots));
    }
}
