package br.ufrj.caronae;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

//    public static String getResponseBody(Response response) {
//        BufferedReader reader;
//        StringBuilder sb = new StringBuilder();
//        try {
//            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
//
//            String line;
//
//            try {
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return sb.toString();
//    }

    public static void toast(int msg) {
        Toast.makeText(App.inst(), App.inst().getString(msg), Toast.LENGTH_SHORT).show();
    }

    public static void toast(String msg) {
        Toast.makeText(App.inst(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void snack(View coordinator, String msg){
        Snackbar.make(coordinator, msg, Snackbar.LENGTH_LONG).show();
    }

    public static String[] getZones() {
        return new String[]{"Centro", "Zona Sul", "Zona Oeste", "Zona Norte", "Baixada", "Grande Niterói", "Outros"};
    }

    public static String[] getNeighborhoods(String zone) {
        if (zone.equals("Centro")) {
            return new String[]{"Benfica", "Caju", "Catumbi", "Centro (Bairro)", "Cidade Nova",
                    "Estácio", "Gamboa", "Glória", "Lapa", "Mangueira", "Rio Comprido",
                    "Santa Teresa", "Santo Cristo", "São Cristóvão", "Saúde", "Vasco da Gama"};
        }
        if (zone.equals("Zona Sul")) {
            return new String[]{"Botafogo", "Catete", "Copacabana", "Cosme Velho",
                    "Flamengo", "Gávea", "Humaitá", "Ipanema", "Jardim Botânico", "Lagoa",
                    "Laranjeiras", "Leblon", "Leme", "Rocinha", "São Conrado", "Urca", "Vidigal"};
        }
        if (zone.equals("Zona Oeste")) {
            return new String[]{"Anil", "Bangu", "Barra de Guaratiba",
                    "Barra da Tijuca", "Camorim", "Campo Grande", "Cidade de Deus", "Cosmos",
                    "Curicica", "Deodoro", "Freguesia de Jacarepaguá", "Gardênia Azul", "Gericinó",
                    "Grumari", "Guaratiba", "Inhoaíba", "Itanhangá", "Jacarepaguá",
                    "Jardim Sulacap", "Joá", "Magalhães Bastos", "Paciência", "Padre Miguel",
                    "Pedra de Guaratiba", "Praça Seca ", "Pechincha", "Realengo",
                    "Recreio dos Bandeirantes", "Santa Cruz", "Santíssimo", "Senador Camará",
                    "Senador Vasconcelos", "Sepetiba", "Tanque", "Taquara", "Vargem Grande",
                    "Vargem Pequena", "Vila Militar", "Vila Valqueire"};
        }
        if (zone.equals("Zona Norte")) {
            return new String[]{"Abolição", "Acari", "Água Santa", "Alto da Boa Vista",
                    "Anchieta", "Andaraí", "Bancários", "Barros Filho", "Bento Ribeiro",
                    "Bonsucesso", "Brás de Pina", "Cachambi", "Cacuia", "Campinho", "Cascadura",
                    "Cavalcanti", "Cocotá", "Coelho Neto", "Colégio",
                    "Complexo do Alemão", "Cordovil", "Costa Barros", "Del Castilho", "Encantado",
                    "Engenheiro Leal", "Engenho Novo", "Engenho da Rainha", "Engenho de Dentro",
                    "Freguesia (Ilha do Governador)", "Galeão", "Grajaú", "Guadalupe",
                    "Higienópolis", "Honório Gurgel", "Inhaúma", "Irajá", "Jacarezinho", "Jacaré",
                    "Jardim América", "Jardim Carioca", "Jardim Guanabara", "Lins de Vasconcelos",
                    "Madureira", "Manguinhos", "Maracanã", "Marechal Hermes", "Maria da Graça",
                    "Maré", "Monero", "Méier", "Olaria", "Oswaldo Cruz", "Parada de Lucas",
                    "Parque Colúmbia", "Pavuna", "Penha", "Penha Circular", "Piedade", "Pilares",
                    "Pitangueiras", "Portuguesa", "Praia da Bandeira", "Praça da Bandeira",
                    "Quintino Bocaiuva", "Ramos", "Riachuelo", "Ribeira", "Ricardo de Albuquerque",
                    "Rocha", "Rocha Miranda", "Sampaio", "São Francisco Xavier", "Tauá", "Tijuca",
                    "Todos os Santos", "Tomás Coelho", "Turiaçu", "Vaz Lobo",
                    "Vicente de Carvalho", "Vigário Geral", "Vila Isabel", "Vila Kosmos",
                    "Vila da Penha", "Vista Alegre", "Zumbi"};
        }
        if (zone.equals("Baixada")) {
            return new String[]{"Belford Roxo", "Duque de Caxias", "Guapimirim", "Itaguai",
                    "Japeri", "Magé", "Mesquita", "Nilópolis", "Nova Iguaçu", "Paracambi",
                    "Queimados", "São João de Meriti", "Seropédica"};
        }
        if (zone.equals("Grande Niterói")) {
            return new String[]{"Itaboraí", "Maricá", "Centro (Niterói)",
                    "Região oceânica (Niterói)", "Rio Bonito", "São Gonçalo", "Tanguá"};
        }
        return new String[]{"Todos os Bairros"};
    }

    public static String[] getAllNeighborhoods() {
        return new String[]{"Benfica", "Caju", "Catumbi", "Centro (Bairro)", "Cidade Nova",
                "Estácio", "Gamboa", "Glória", "Lapa", "Mangueira", "Rio Comprido",
                "Santa Teresa", "Santo Cristo", "São Cristóvão", "Saúde", "Vasco da Gama",
                "Botafogo", "Catete", "Copacabana", "Cosme Velho",
                "Flamengo", "Gávea", "Humaitá", "Ipanema", "Jardim Botânico", "Lagoa",
                "Laranjeiras", "Leblon", "Leme", "Rocinha", "São Conrado", "Urca", "Vidigal",
                "Anil", "Bangu", "Barra de Guaratiba",
                "Barra da Tijuca", "Camorim", "Campo Grande", "Cidade de Deus", "Cosmos",
                "Curicica", "Deodoro", "Freguesia de Jacarepaguá", "Gardênia Azul", "Gericinó",
                "Grumari", "Guaratiba", "Inhoaíba", "Itanhangá", "Jacarepaguá",
                "Jardim Sulacap", "Joá", "Magalhães Bastos", "Paciência", "Padre Miguel",
                "Pedra de Guaratiba", "Praça Seca ", "Pechincha", "Realengo",
                "Recreio dos Bandeirantes", "Santa Cruz", "Santíssimo", "Senador Camará",
                "Senador Vasconcelos", "Sepetiba", "Tanque", "Taquara", "Vargem Grande",
                "Vargem Pequena", "Vila Militar", "Vila Valqueire",
                "Abolição", "Acari", "Água Santa", "Alto da Boa Vista",
                "Anchieta", "Andaraí", "Bancários", "Barros Filho", "Bento Ribeiro",
                "Bonsucesso", "Brás de Pina", "Cachambi", "Cacuia", "Campinho", "Cascadura",
                "Cavalcanti", "Cocotá", "Coelho Neto", "Colégio",
                "Complexo do Alemão", "Cordovil", "Costa Barros", "Del Castilho", "Encantado",
                "Engenheiro Leal", "Engenho Novo", "Engenho da Rainha", "Engenho de Dentro",
                "Freguesia (Ilha do Governador)", "Galeão", "Grajaú", "Guadalupe",
                "Higienópolis", "Honório Gurgel", "Inhaúma", "Irajá", "Jacarezinho", "Jacaré",
                "Jardim América", "Jardim Carioca", "Jardim Guanabara", "Lins de Vasconcelos",
                "Madureira", "Manguinhos", "Maracanã", "Marechal Hermes", "Maria da Graça",
                "Maré", "Monero", "Méier", "Olaria", "Oswaldo Cruz", "Parada de Lucas",
                "Parque Colúmbia", "Pavuna", "Penha", "Penha Circular", "Piedade", "Pilares",
                "Pitangueiras", "Portuguesa", "Praia da Bandeira", "Praça da Bandeira",
                "Quintino Bocaiuva", "Ramos", "Riachuelo", "Ribeira", "Ricardo de Albuquerque",
                "Rocha", "Rocha Miranda", "Sampaio", "São Francisco Xavier", "Tauá", "Tijuca",
                "Todos os Santos", "Tomás Coelho", "Turiaçu", "Vaz Lobo",
                "Vicente de Carvalho", "Vigário Geral", "Vila Isabel", "Vila Kosmos",
                "Vila da Penha", "Vista Alegre", "Zumbi",
                "Belford Roxo", "Duque de Caxias", "Guapimirim", "Itaguai",
                "Japeri", "Magé", "Mesquita", "Nilópolis", "Nova Iguaçu", "Paracambi",
                "Queimados", "São João de Meriti", "Seropédica",
                "Itaboraí", "Maricá", "Centro (Niterói)",
                "Região oceânica (Niterói)", "Rio Bonito", "São Gonçalo", "Tanguá"};
    }

    public static String[] getAllNeighborhoodsLowerCase() {
        String[] neighborhoods = getAllNeighborhoods();
        for (int index = 0; index < neighborhoods.length; index++) {
            neighborhoods[index] = neighborhoods[index].toLowerCase();
        }
        return neighborhoods;
    }

    public static String[] getHubs() {
        return new String[]{"CCMN: Frente", "CCMN: Fundos", "CCS: Frente", "CCS: HUCFF", "CT: Bloco A", "CT: Bloco D", "CT: Bloco H", "EEFD", "Letras", "Reitoria"};
    }

    public static String[] getCenters() {
        return new String[]{"Todos os Centros", "CCMN", "CCS", "CT", "EEFD", "Letras", "Reitoria"};
    }

    public static String[] getCentersLowerCase() {
        String[] center = getCenters();
        for (int index = 0; index < center.length; index++) {
            center[index] = center[index].toLowerCase();
        }
        return center;
    }

    public static String[] getCentersWithoutAllCenters() {
        return new String[]{"CCMN", "CCS", "CT", "EEFD", "Letras", "Reitoria"};
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

    public static String formatGoodDateWithoutYear(String date) {
        String formattedTime = "";
        try {
            Date date2 = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(date);
            formattedTime = new SimpleDateFormat("dd/MM", Locale.US).format(date2);
        } catch (ParseException e) {
            e.printStackTrace();
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

    // Input Date Format: "YYYY-MM-DD"
    public static int getDayFromDate(String date) {
        return Integer.parseInt(date.substring(8, 10));
    }

    // Input Date Format: "YYYY-MM-DD"
    // Return DD/MM
    public static String getDayWithMonthFromDate(String date) {
        return date.substring(8, 10) + "/" + date.substring(5, 7);
    }

    // Input Date Format: "YYYY-MM-DD"
    public static String getWeekDayFromDate(String dateString) {
        int dayOfWeekInt = -1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar c = Calendar.getInstance();
            Date today = c.getTime();
            c.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = c.getTime();
            String currentDate = format.format(today);
            String tomorrowString = format.format(tomorrow);
            if (currentDate.equals(dateString)){
                return "Hoje";
            }if (tomorrowString.equals(dateString)){
                return "Amanhã";
            }
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
        return dayOfWeek;
    }

    // Input Date Format: "YYYY-MM-DD"
    public static int getDaysBetweenTwoDates(String date1String, String date2String) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date1 = format.parse(date1String);
            Date date2 = format.parse(date2String);
            long diff = date2.getTime() - date1.getTime();
            return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String fixBlankSpace(String word) {
        return word.replace(" ", "");
    }

    public static int convertDpToPixel(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.inst().getResources().getDisplayMetrics());
    }

    public static String getHeaderForHttp(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        return "Caronae/" + pInfo.versionCode + "(Android; " + Build.VERSION.CODENAME + ";";
        return "Caronae/"
                + Util.getAppVersionName(context)
                + "("
                + Build.BRAND
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

    public static class BlurBuilder {
        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 24.5f;

        public static Bitmap blur(View v) {
            return blur(v.getContext(), getScreenshot(v));
        }

        public static Bitmap blur(Context ctx, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(ctx);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }

        private static Bitmap getScreenshot(View v) {
            Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.draw(c);
            return b;
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

    // Return String[]{Bairro, dia, hora, centro}
    public static String[] searchAlgorithin(String input, String[] list) {
        input = input.toLowerCase();
        String[] inputs = input.split(" ");
        String[] results = new String[4];
        String[] neighborhoods = getAllNeighborhoods();
        String[] neighborhoodsLowerCase = getAllNeighborhoodsLowerCase();
        String[] centers = getCenters();
        String[] centersLowerCase = getCentersLowerCase();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date todayDate = new Date();
        String date = simpleDateFormat.format(todayDate);

        simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        String hour = simpleDateFormat.format(new Date());


        for (int i = 0; i < inputs.length; i++) {
//            inputs[i] = inputs[i].replace(" ", "");
            char[] c = inputs[i].toCharArray();
            boolean isNumber = false;

            for (int j = 0; j < c.length; j++) {
                if (Character.isDigit(c[j])) {
                    isNumber = true;
                }
            }
            if (inputs[i].contains(":") && isNumber) {
                String[] hourAndMinute = inputs[i].split(":");
                for (int j = 0; j <  hourAndMinute.length; j++){
                    if (j == 0 && Integer.parseInt(hourAndMinute[j]) > 23){
                        hourAndMinute[j] = "23";
                    } else if (j == 1 && Integer.parseInt(hourAndMinute[j]) > 59){
                        hourAndMinute[j] = "59";
                    }
                    if (hourAndMinute.length == 1){
                        hour = hourAndMinute[0] + ":00";
                    } else {
                        hour = hourAndMinute[0] + ":" + hourAndMinute[1];
                    }
                }
            }

            if (inputs[i].contains("/") && isNumber) {
                String[] dayAndMonth = inputs[i].split("/");
                for (int j = 0; i <  dayAndMonth.length; j++){
                    if (j == 0 && Integer.parseInt(dayAndMonth[j]) > 31){
                        dayAndMonth[j] = "31";
                        date = dayAndMonth[j] + "/";
                    }
                    if (j == 1 && Integer.parseInt(dayAndMonth[j]) > 12){
                        dayAndMonth[j] = "12";
                        date = date + dayAndMonth[j];
                    }
                    if (j == 1 && Integer.parseInt(dayAndMonth[j]) == 0){
                        dayAndMonth[j] = "1";
                        date = date + dayAndMonth[j];
                    }
                    if (dayAndMonth.length == 1){
                        date = dayAndMonth[0] + "/01/2017";
                    } else {
                        date = dayAndMonth[0] + "/" + dayAndMonth[1] + "/" + "2017";
                    }
                }
            }

            if (!(inputs[i].contains(":")) && !(inputs[i].contains("/")) && isNumber){
                hour = inputs[i] + ":00";
                date = inputs[i] + date.substring(2, date.length());
            }
        }

        for (int j = 0; j < inputs.length; j++) {
            for (int i = 0; i < neighborhoodsLowerCase.length; i++) {
                if (neighborhoodsLowerCase[i].contains(inputs[j])) {
                    results[0] = neighborhoods[i];
                }
            }
        }

        for (int j = 0; j < inputs.length; j++) {
            for (int i = 0; i < centersLowerCase.length; i++) {
                if (centersLowerCase[i].contains(inputs[j])) {
                    results[3] = centers[i];
                }
            }
        }

        for (int i = 0; i < results.length; i++) {
            if (results[i] == null) {
                results[i] = "";
            }
        }

        results[1] = date;
        results[2] = hour;

        return results;
    }

    public static Bitmap getCircularBitmapWithWhiteBorder(Bitmap bitmap,
                                                          int borderWidth,
                                                          int borderColor) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        final int width = bitmap.getWidth() + borderWidth;
        final int height = bitmap.getHeight() + borderWidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(borderColor);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }

    static public int getBgResByZone(String zone){
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

    static public int getColorbyZone(String zone){
        int color = ContextCompat.getColor(App.inst(), R.color.zone_outros);
        if (zone.equals("Centro")) {
            color = ContextCompat.getColor(App.inst(), R.color.zone_centro);
        }
        if (zone.equals("Zona Sul")) {
            color = ContextCompat.getColor(App.inst(), R.color.zone_sul);
        }
        if (zone.equals("Zona Oeste")) {
            color = ContextCompat.getColor(App.inst(), R.color.zone_oeste);
        }
        if (zone.equals("Zona Norte")) {
            color = ContextCompat.getColor(App.inst(), R.color.zone_norte);
        }
        if (zone.equals("Baixada")) {
            color = ContextCompat.getColor(App.inst(), R.color.zone_baixada);
        }
        if (zone.equals("Grande Niterói")) {
            color = ContextCompat.getColor(App.inst(), R.color.zone_niteroi);
        }
        return color;
    }

    static public int getPressedColorbyZone(String zone){
        int color = ContextCompat.getColor(App.inst(), R.color.zone_outros);
        if (zone.equals("Centro")) {
            color = ContextCompat.getColor(App.inst(), R.color.light_zone_centro_transparency);
        }
        if (zone.equals("Zona Sul")) {
            color = ContextCompat.getColor(App.inst(), R.color.light_zone_sul_transparency);
        }
        if (zone.equals("Zona Oeste")) {
            color = ContextCompat.getColor(App.inst(), R.color.light_zone_oeste_transparency);
        }
        if (zone.equals("Zona Norte")) {
            color = ContextCompat.getColor(App.inst(), R.color.light_zone_norte_transparency);
        }
        if (zone.equals("Baixada")) {
            color = ContextCompat.getColor(App.inst(), R.color.light_zone_baixada_transparency);
        }
        if (zone.equals("Grande Niterói")) {
            color = ContextCompat.getColor(App.inst(), R.color.light_zone_niteroi_transparency);
        }
        return color;
    }

    static public int getPressedColorbyNormalColor(int color){
        int PressedColor = ContextCompat.getColor(App.inst(), R.color.zone_outros);
        if (color == ContextCompat.getColor(App.inst(),R.color.zone_centro)) {
            PressedColor = ContextCompat.getColor(App.inst(), R.color.light_zone_centro_transparency);
        }
        if (color == ContextCompat.getColor(App.inst(),R.color.zone_sul)) {
            PressedColor = ContextCompat.getColor(App.inst(), R.color.light_zone_sul_transparency);
        }
        if (color == ContextCompat.getColor(App.inst(),R.color.zone_oeste)) {
            PressedColor = ContextCompat.getColor(App.inst(), R.color.light_zone_oeste_transparency);
        }
        if (color == ContextCompat.getColor(App.inst(),R.color.zone_norte)) {
            PressedColor = ContextCompat.getColor(App.inst(), R.color.light_zone_norte_transparency);
        }
        if (color == ContextCompat.getColor(App.inst(),R.color.zone_baixada)) {
            PressedColor = ContextCompat.getColor(App.inst(), R.color.light_zone_baixada_transparency);
        }
        if (color == ContextCompat.getColor(App.inst(),R.color.zone_niteroi)) {
            PressedColor = ContextCompat.getColor(App.inst(), R.color.light_zone_niteroi_transparency);
        }
        return PressedColor;
    }

    public static void treatResponseFromServer(Response response){
        if (response.code() == 401){
            Util.toast(R.string.invalidToken);
            App.LogOut();
        }
    }
}
