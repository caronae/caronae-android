package br.ufrj.caronae;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import retrofit2.Response;


public class Util {

    public static void expandOrCollapse(final View v, boolean expand) {
        TranslateAnimation anim;
        if (expand) {
            anim = new TranslateAnimation(0.0f, 0.0f, -v.getHeight(), 0.0f);
            v.setVisibility(View.VISIBLE);
        } else {
            anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -v.getHeight());
            Animation.AnimationListener collapselistener = new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.GONE);
                }
            };

            anim.setAnimationListener(collapselistener);
        }

        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator(0.5f));
        v.startAnimation(anim);
    }

    public static void toast(int msg) {
        Toast.makeText(App.getInst(), App.getInst().getString(msg), Toast.LENGTH_SHORT).show();
    }

    public static void toast(String msg) {
        Toast.makeText(App.getInst(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void debug(String msg)
    {
        Log.d("DEBUG: ", msg);
    }

    public static String[] getFundaoHubs() {
        return new String[]{"CCMN: Frente", "CCMN: Fundos", "CCS: Frente", "CCS: HUCFF", "CT: Bloco A", "CT: Bloco D", "CT: Bloco H", "EEFD", "Letras", "Reitoria"};
    }

    public static String[] getFundaoCenters() {
        return new String[]{"Todos os Centros", "CCMN", "CCS", "CT", "EEFD", "Letras", "Reitoria"};
    }

    public static String[] getPraiaVermelhaHubs() {
        return new String[]{"Praia Vermelha: Pinel-Fundos", "Praia Vermelha: Psicologia"};

    }

    public static String[] getCampi() {
        return new String[]{"Todos os Centros", "Cidade Universitária", "Praia Vermelha"};
    }

    public static int getNumberAvailableCampis() {
        return 2;
    }

    public static String[] getCampiHubsByIndex(int index) {
        switch (index) {
            case 0:
                return getFundaoHubs();
            case 1:
                return getPraiaVermelhaHubs();
            default:
                return getFundaoHubs();
        }
    }

    public static ArrayList<ArrayList<String>> getListAvailableHubs() {
        ArrayList<ArrayList<String>> hubs = new ArrayList<>();
        for (int i = 0; i < getNumberAvailableCampis(); i++) {
            String[] campiHubs = getCampiHubsByIndex(i);
            for (int j = 0; j < campiHubs.length; j++) {
                hubs.get(i).add(campiHubs[j]);
            }
        }
        return hubs;
    }

    public static String formatTime(String time) {
        String formattedTime = "";
        try {
            Date date = new SimpleDateFormat("HH:mm:ss", Locale.US).parse(time);
            formattedTime = new SimpleDateFormat("HH:mm", Locale.US).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
    }

    public static String formatBadDateWithYear(String date) {
        String formattedTime = "";
        try {
            Date date2 = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(date);
            formattedTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date2);
        } catch (ParseException e) {
            try {
                Date date2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
                formattedTime = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(date2);
            } catch (ParseException ex) {
                e.printStackTrace();
            }
        }
        return formattedTime;
    }

    public static String formatBadHour(String hour) {
        return hour.substring(0, hour.length() - 3);
    }

    public static String formatBadDateWithoutYear(String date) {
        String formattedTime = "";
        try {
            Date date2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
            formattedTime = new SimpleDateFormat("dd/MM", Locale.US).format(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
    }

    public static String formatDateRemoveYear(String date) {
        return date.substring(0, 5);
    }

    // Input Date Format: "YYYY-MM-DD"
    public static int getDayFromDate(String date) {
        return Integer.parseInt(date.substring(8, 10));
    }

    public static String getWeekDayFromBRDate(String dateString) {
        int dayOfWeekInt = -1;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try
        {
            Calendar c = Calendar.getInstance();
            Date date = format.parse(dateString);
            c.setTime(date);
            dayOfWeekInt = c.get(Calendar.DAY_OF_WEEK);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        String dayOfWeek = "";

        switch (dayOfWeekInt) {
            case 1:
                dayOfWeek = "domingo";
                break;
            case 2:
                dayOfWeek = "segunda-feira";
                break;
            case 3:
                dayOfWeek = "terça-feira";
                break;
            case 4:
                dayOfWeek = "quarta-feira";
                break;
            case 5:
                dayOfWeek = "quinta-feira";
                break;
            case 6:
                dayOfWeek = "sexta-feira";
                break;
            case 7:
                dayOfWeek = "sábado";
                break;
        }
        return dayOfWeek;
    }

    public static String getWeekDayFromDateWithoutTodayString(String dateString) {
        int dayOfWeekInt = -1;
        if (dateString.contains("/"))
            dateString = formatBadDateWithYear(dateString);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar c = Calendar.getInstance();
            Date today = c.getTime();
            c.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = c.getTime();
            Date date = format.parse(dateString);
            c.setTime(date);
            dayOfWeekInt = c.get(Calendar.DAY_OF_WEEK);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String dayOfWeek = "";

        switch (dayOfWeekInt) {
            case 1:
                dayOfWeek = "Domingo";
                break;
            case 2:
                dayOfWeek = "Segunda-Feira";
                break;
            case 3:
                dayOfWeek = "Terça-Feira";
                break;
            case 4:
                dayOfWeek = "Quarta-Feira";
                break;
            case 5:
                dayOfWeek = "Quinta-Feira";
                break;
            case 6:
                dayOfWeek = "Sexta-Feira";
                break;
            case 7:
                dayOfWeek = "Sábado";
                break;
        }
        return dayOfWeek.substring(0, 3);
    }

    public static String fixBlankSpaces(String word) {
        return word.replace(" ", "").trim();
    }

    public static String getHeaderForHttp(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String brand = Build.BRAND;
        brand = brand.substring(0, 1).toUpperCase() + brand.substring(1, brand.length());
        return "Caronae/"
                + Util.getAppVersionName(context)
                + " ("
                + brand
                + ": "
                + android.os.Build.MODEL
                + "; "
                + "Android: "
                + Build.VERSION.RELEASE
                + ")";
    }

    public static String getAppVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Version Not Found";
        }
    }

    public static class OffsetDecoration extends RecyclerView.ItemDecoration {
        private int mBottomOffset;
        private int mTopOffset;

        public OffsetDecoration(int bottomOffset, int topOffset) {
            mBottomOffset = bottomOffset;
            mTopOffset = topOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int dataSize = state.getItemCount();
            int position = parent.getChildAdapterPosition(view);
            if (dataSize > 0 && position == dataSize - 1) {
                outRect.set(0, 0, 0, mBottomOffset);
            }
            if (dataSize > 0 && position == 0) {
                outRect.set(0, mTopOffset, 0, 0);
            }
        }

    }

    static public int getBgResByZone(String zone) {
        int bgRes = R.drawable.bg_bt_raise_zone_outros;
        if (zone.equals("Centro")) {
            bgRes = R.drawable.bg_bt_raise_zone_centro;
        }
        if (zone.equals("Zona Sul")) {
            bgRes = R.drawable.bg_bt_raise_zone_sul;
        }
        if (zone.equals("Zona Oeste")) {
            bgRes = R.drawable.bg_bt_raise_zone_oeste;
        }
        if (zone.equals("Zona Norte")) {
            bgRes = R.drawable.bg_bt_raise_zone_norte;
        }
        if (zone.equals("Baixada")) {
            bgRes = R.drawable.bg_bt_raise_zone_baixada;
        }
        if (zone.equals("Grande Niterói")) {
            bgRes = R.drawable.bg_bt_raise_zone_niteroi;
        }
        return bgRes;
    }

    static public int getColorbyZone(String zone) {
        int color = ContextCompat.getColor(App.getInst(), R.color.zone_outros);
        if (zone.equals("Centro")) {
            color = ContextCompat.getColor(App.getInst(), R.color.zone_centro);
        }
        if (zone.equals("Zona Sul")) {
            color = ContextCompat.getColor(App.getInst(), R.color.zone_sul);
        }
        if (zone.equals("Zona Oeste")) {
            color = ContextCompat.getColor(App.getInst(), R.color.zone_oeste);
        }
        if (zone.equals("Zona Norte")) {
            color = ContextCompat.getColor(App.getInst(), R.color.zone_norte);
        }
        if (zone.equals("Baixada")) {
            color = ContextCompat.getColor(App.getInst(), R.color.zone_baixada);
        }
        if (zone.equals("Grande Niterói")) {
            color = ContextCompat.getColor(App.getInst(), R.color.zone_niteroi);
        }
        return color;
    }

    static public int getPressedColorbyNormalColor(int color) {
        int PressedColor = ContextCompat.getColor(App.getInst(), R.color.zone_outros);
        if (color == ContextCompat.getColor(App.getInst(), R.color.zone_centro)) {
            PressedColor = ContextCompat.getColor(App.getInst(), R.color.light_zone_centro_transparency);
        }
        if (color == ContextCompat.getColor(App.getInst(), R.color.zone_sul)) {
            PressedColor = ContextCompat.getColor(App.getInst(), R.color.light_zone_sul_transparency);
        }
        if (color == ContextCompat.getColor(App.getInst(), R.color.zone_oeste)) {
            PressedColor = ContextCompat.getColor(App.getInst(), R.color.light_zone_oeste_transparency);
        }
        if (color == ContextCompat.getColor(App.getInst(), R.color.zone_norte)) {
            PressedColor = ContextCompat.getColor(App.getInst(), R.color.light_zone_norte_transparency);
        }
        if (color == ContextCompat.getColor(App.getInst(), R.color.zone_baixada)) {
            PressedColor = ContextCompat.getColor(App.getInst(), R.color.light_zone_baixada_transparency);
        }
        if (color == ContextCompat.getColor(App.getInst(), R.color.zone_niteroi)) {
            PressedColor = ContextCompat.getColor(App.getInst(), R.color.light_zone_niteroi_transparency);
        }
        return PressedColor;
    }

    static public void createChatAssets(Ride ride, Context context) {
        Ride rideWithUsers = ride;
        int color = 0, bgRes = 0;
        if(rideWithUsers.getZone().isEmpty()) {
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

    public static void treatResponseFromServer(Response response) {
        if (response.code() == 401) {
            Util.toast(R.string.invalidToken);
            App.LogOut();
        }
    }

    public static String getTextToShareRide(RideForJson ride) {
        String text;

        if (ride.isGoing()) {
            text = "Carona: " + ride.getNeighborhood() + " → " + ride.getHub() + "\n"
                    + "Chegando às " + formatTime(ride.getTime()) + " | " + Util.getWeekDayFromDateWithoutTodayString(ride.getDate()) + " | " + formatDateRemoveYear(formatBadDateWithYear(ride.getDate())) + "\n"
                    + Constants.SHARE_LINK + ride.getDbId();
        } else {
            text = "Carona: " + ride.getHub() + " → " + ride.getNeighborhood() + "\n"
                    + "Saíndo às " + formatTime(ride.getTime()) + " | " + Util.getWeekDayFromDateWithoutTodayString(ride.getDate()) + " | " + formatDateRemoveYear(formatBadDateWithYear(ride.getDate())) + "\n"
                    + Constants.SHARE_LINK + ride.getDbId();
        }

        return text;
    }

    public static String getWeekDayFromDate(String dateString) {
        int dayOfWeekInt = -1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, 1);
            Date date = format.parse(dateString);
            c.setTime(date);
            dayOfWeekInt = c.get(Calendar.DAY_OF_WEEK);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String dayOfWeek = "";

        switch (dayOfWeekInt) {
            case 1:
                dayOfWeek = "Dom";
                break;
            case 2:
                dayOfWeek = "Seg";
                break;
            case 3:
                dayOfWeek = "Ter";
                break;
            case 4:
                dayOfWeek = "Qua";
                break;
            case 5:
                dayOfWeek = "Qui";
                break;
            case 6:
                dayOfWeek = "Sex";
                break;
            case 7:
                dayOfWeek = "Sáb";
                break;
        }
        return dayOfWeek;
    }

    public static String getTextToShareRide(Ride ride) {
        String text;

        String dayMonth = "";
        if (ride.getDate().contains("/")){
            dayMonth = formatDateRemoveYear(ride.getDate());
        } else {
            dayMonth = formatBadDateWithYear(ride.getDate());
        }

        if (ride.isGoing()) {
            text = "Carona: " + ride.getNeighborhood() + " → " + ride.getHub() + "\n"
                    + "Chegando às " + formatTime(ride.getTime())
                    + " | " + Util.getWeekDayFromDateWithoutTodayString(ride.getDate())
                    + " | " + dayMonth + "\n"
                    + Constants.SHARE_LINK + ride.getDbId();
        } else {
            text = "Carona: " + ride.getHub() + " → " + ride.getNeighborhood() + "\n"
                    + "Saíndo às " + formatTime(ride.getTime())
                    + " | " + Util.getWeekDayFromDateWithoutTodayString(ride.getDate())
                    + " | " + dayMonth + "\n"
                    + Constants.SHARE_LINK + ride.getDbId();
        }

        return text;
    }

    public static long getStringDateInMillis(String date){
        try {
            Date dateString = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd", Locale.ENGLISH).parse(date);
            return dateString.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
