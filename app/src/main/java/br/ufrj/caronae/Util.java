package br.ufrj.caronae;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.modelsforjson.PlacesForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Util {

    private static Map<String, Integer> colorZone = new TreeMap<>();
    private static ArrayList<String> zones = new ArrayList<>();
    private static ArrayList<String> campus = new ArrayList<>();
    private static Map<String, String> findNeigh = new TreeMap<>();
    private static boolean colorsSaved;

    public static void setColors()
    {
        if(Util.colorZone.isEmpty()) {
            if(SharedPref.checkExistence(SharedPref.PLACE_KEY))
            {
                int color;
                PlacesForJson places = SharedPref.getPlace();
                for (int i = 0; i < places.getZones().size(); i++) {
                    color = Color.parseColor(places.getZones().get(i).getColor());
                    colorZone.put(places.getZones().get(i).getName(), color);
                }
                color = Color.parseColor("#565658");
                colorZone.put("Outros", color);
                colorsSaved = true;
            }
            else {
                CaronaeAPI.service(App.getInst()).getPlaces()
                        .enqueue(new Callback<PlacesForJson>() {
                            @Override
                            public void onResponse(Call<PlacesForJson> call, Response<PlacesForJson> response) {
                                if (response.isSuccessful()) {
                                    PlacesForJson places = response.body();
                                    SharedPref.setPlace(places);
                                    int color;
                                    for (int i = 0; i < places.getZones().size(); i++) {
                                        color = Color.parseColor(places.getZones().get(i).getColor());
                                        colorZone.put(places.getZones().get(i).getName(), color);
                                    }
                                    color = Color.parseColor("#565658");
                                    colorZone.put("Outros", color);
                                    colorsSaved = true;
                                }
                            }

                            @Override
                            public void onFailure(Call<PlacesForJson> call, Throwable t) {
                                Log.e("ERROR: ", t.getMessage());
                            }
                        });
            }
        }
    }

    public static boolean isZone(String location)
    {
        if(zones.isEmpty())
        {
            if(SharedPref.checkExistence(SharedPref.PLACE_KEY))
            {
                PlacesForJson places = SharedPref.getPlace();
                for (int i = 0; i < places.getZones().size(); i++) {
                   zones.add(places.getZones().get(i).getName());
                }
                zones.add("Outros");
            }
        }
        return zones.contains(location);
    }

    public static String whichZone(String neighborhood)
    {
        if(findNeigh.isEmpty())
        {
            if (SharedPref.checkExistence(SharedPref.PLACE_KEY)) {
                PlacesForJson places = SharedPref.getPlace();
                for(int i = 0; i < places.getZones().size(); i++)
                {
                    for(int j = 0; j < places.getZones().get(i).getNeighborhoods().size(); j++)
                    {
                        findNeigh.put(places.getZones().get(i).getNeighborhoods().get(j), places.getZones().get(i).getName());
                    }
                }
            }
        }
        if(findNeigh.containsKey(neighborhood)) {
            return findNeigh.get(neighborhood);
        }
        else
        {
            return "Outros";
        }
    }

    public static boolean isCampus(String campi)
    {
        if(campus.isEmpty())
        {
            if(SharedPref.checkExistence(SharedPref.PLACE_KEY))
            {
                PlacesForJson places = SharedPref.getPlace();
                for (int i = 0; i < places.getCampi().size(); i++) {
                    campus.add(places.getCampi().get(i).getName());
                }
            }
        }
        return campus.contains(campi);
    }

    public static int getColors(String key)
    {
        while(!colorsSaved)
        {
            Util.setColors();
        }
        if(colorZone.containsKey(key)) {
            return colorZone.get(key);
        }
        else
        {
            return Color.parseColor("#565658");
        }
    }

    public static void toast(int msg) {
        Toast.makeText(App.getInst(), App.getInst().getString(msg), Toast.LENGTH_SHORT).show();
    }

    public static void toast(String msg) {
        Toast.makeText(App.getInst(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void debug(String msg)
    {
        Log.e("DEBUG: ", msg);
    }

    public static void debug(int msg)
    {
        Log.e("DEBUG: ", Integer.toString(msg));
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
        PackageInfo info;
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

    public static void treatResponseFromServer(Response response) {
        if (response.code() == 401) {
            Util.toast(R.string.invalid_token);
            App.LogOut();
        }
    }

    public static String getTextToShareRide(RideForJson ride, int id) {
        String text;

        if (ride.isGoing()) {
            text = "Carona: " + ride.getNeighborhood() + " → " + ride.getHub() + "\n"
                    + "Chegando às " + formatTime(ride.getTime()) + " | " + Util.getWeekDayFromDateWithoutTodayString(ride.getDate()) + " | " + formatDateRemoveYear(formatBadDateWithYear(ride.getDate())) + "\n"
                    + Constants.SHARE_LINK + id;
        } else {
            text = "Carona: " + ride.getHub() + " → " + ride.getNeighborhood() + "\n"
                    + "Saíndo às " + formatTime(ride.getTime()) + " | " + Util.getWeekDayFromDateWithoutTodayString(ride.getDate()) + " | " + formatDateRemoveYear(formatBadDateWithYear(ride.getDate())) + "\n"
                    + Constants.SHARE_LINK + id;
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
