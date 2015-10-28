package br.ufrj.caronae;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orm.SugarApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import br.ufrj.caronae.components.DaggerNetworkComponent;
import br.ufrj.caronae.components.NetworkComponent;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.modules.NetworkModule;
import retrofit.client.Response;

public class App extends SugarApp {

    public static final String USER_PREF_KEY = "user";
    public static final String MISSING_PREF = "missing";
    public static final String LAST_RIDE_OFFER_PREF_KEY = "lastRideOffer";
    public static final String LAST_RIDE_SEARCH_FILTERS_PREF_KEY = "lastRideSearchFilters";
    public static final String TOKEN_PREF_KEY = "token";

    public static final String APIARY_ENDPOINT = "http://private-5b9ed6-caronae.apiary-mock.com";
    public static final String LUISDIGOCEAN_ENDPOINT = "http://104.131.31.224/";
    public static final String LOCAL_SERV_ENDPOINT = "http://192.168.0.13/";

    private static App inst;
    private static User user;
    private static NetworkComponent component;
    private static NetworkComponent component2;

    public App() {
        inst = this;
    }

    public static App inst() {
        return inst;
    }

    public static User getUser() {
        if (user == null) {
            String userJson = getPref(USER_PREF_KEY);
            if (!userJson.equals(MISSING_PREF))
                user = new Gson().fromJson(userJson, User.class);
        }

        return user;
    }

    public static boolean isUserLoggedIn() {
        return getUser() != null;
    }

    public static void logOut() {
        user = null;
        removePref(USER_PREF_KEY);
        removePref(LAST_RIDE_OFFER_PREF_KEY);
        removePref(LAST_RIDE_SEARCH_FILTERS_PREF_KEY);
        Ride.deleteAll(Ride.class);
    }

    public static void saveUser(User user) {
        putPref(USER_PREF_KEY, new Gson().toJson(user));
    }

    public static String getUserToken() {
        return getPref(TOKEN_PREF_KEY);
    }

    public static void saveToken(String token) {
        putPref(TOKEN_PREF_KEY, token);
    }

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(inst());
    }

    private static SharedPreferences.Editor getSharedPrefEditor() {
        return getSharedPreferences().edit();
    }

    public static void putPref(String key, String value) {
        getSharedPrefEditor().putString(key, value).apply();
    }

    public static void removePref(String key) {
        getSharedPrefEditor().remove(key).apply();
    }

    public static String getPref(String key) {
        return getSharedPreferences().getString(key, MISSING_PREF);
    }

    public static NetworkService getNetworkService() {
        if (component == null) {
            //String endpoint = LUISDIGOCEAN_ENDPOINT;
            String endpoint = LOCAL_SERV_ENDPOINT;
            component = DaggerNetworkComponent.builder().networkModule(new NetworkModule(endpoint)).build();
        }
        return component.provideNetworkService();
    }

    public static NetworkService getApiaryNetworkService() {
        if (component2 == null)
            component2 = DaggerNetworkComponent.builder().networkModule(new NetworkModule(APIARY_ENDPOINT)).build();
        return component2.provideNetworkService();
    }

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

        // To Collapse
        //

        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator(0.5f));
        v.startAnimation(anim);
    }

    public static String printResponseBody(Response response) {
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static void toast(String msg) {
        Toast.makeText(App.inst(), msg, Toast.LENGTH_SHORT).show();
    }

    public static String[] getZones() {
        return new String[]{"Centro", "Zona Sul", "Zona Oeste", "Zona Norte", "Baixada", "Grande Niterói"};
    }

    public static String[] getNeighborhoods(String zone) {
        if (zone.equals("Centro")) {
            String[] a = new String[]{"São Cristóvão", "Benfica", "Caju", "Catumbi", "Centro", "Cidade Nova", "Estácio", "Gamboa", "Glória", "Lapa", "Mangueira", "Paquetá", "Rio Comprido", "Santa Teresa", "Santo Cristo", "Saúde", "Vasco da Gama"};
            Arrays.sort(a);
            return a;
        }
        if (zone.equals("Zona Sul")) {
            String[] a = new String[]{"Botafogo", "Catete", "Copacabana", "Cosme Velho", "Flamengo", "Gávea", "Humaitá", "Ipanema", "Jardim Botânico", "Lagoa", "Laranjeiras", "Leblon", "Leme", "Rocinha", "São Conrado", "Urca", "Vidigal"};
            Arrays.sort(a);
            return a;
        }
        if (zone.equals("Zona Oeste")) {
            String[] a = new String[]{"Anil", "Barra da Tijuca", "Camorim", "Cidade de Deus", "Curicica", "Freguesia de Jacarepaguá", "Gardênia Azul", "Grumari ", "Itanhangá ", "Jacarepaguá ", "Joá ", "Praça Seca ", "Pechincha ", "Recreio dos Bandeirantes ", "Tanque ", "Taquara ", "Vargem Grande ", "Vargem Pequena ", "Vila Valqueire ", "Bangu ", "Deodoro ", "Gericinó ", "Jardim Sulacap ", "Magalhães Bastos ", "Padre Miguel ", "Realengo ", "Santíssimo ", "Senador Camará ", "Vila Militar ", "Barra de Guaratiba ", "Campo Grande ", "Cosmos ", "Guaratiba ", "Inhoaíba ", "Paciência ", "Pedra de Guaratiba ", "Santa Cruz ", "Senador Vasconcelos", "Sepetiba"};
            Arrays.sort(a);
            return a;
        }
        if (zone.equals("Zona Norte")) {
            String[] a = new String[]{"Alto da Boa Vista", "Andaraí ", "Grajaú ", "Maracanã ", "Praça da Bandeira ", "Tijuca ", "Vila Isabel ", "Abolição ", "Água Santa ", "Cachambi", "Del Castilho", "Encantado", "Engenho de Dentro", "Engenho Novo", "Inhaúma ", "Jacaré ", "Jacarezinho ", "Lins de Vasconcelos ", "Maria da Graça", "Méier ", "Piedade ", "Pilares ", "Riachuelo ", "Rocha ", "Sampaio ", "São Francisco Xavier ", "Todos os Santos ", "Bancários ", "Cacuia ", "Cidade Universitária", "Cocotá ", "Freguesia (Ilha do Governador) ", "Galeão ", "Jardim Carioca ", "Jardim Guanabara ", "Maré ", "Monero ", "Pitangueiras ", "Portuguesa ", "Praia da Bandeira", "Ribeira ", "Tauá ", "Zumbi ", "Acari ", "Anchieta ", "Barros Filho ", "Bento Ribeiro ", "Brás de Pina ", "Bonsucesso ", "Campinho ", "Cavalcanti ", "Cascadura ", "Coelho Neto ", "Colégio ", "Complexo do Alemão ", "Cordovil ", "Costa Barros ", "Engenheiro Leal ", "Engenho da Rainha ", "Guadalupe ", "Higienópolis ", "Honório Gurgel ", "Irajá ", "Jardim América", "Madureira ", "Marechal Hermes ", "Manguinhos ", "Oswaldo Cruz ", "Olaria ", "Parada de Lucas ", "Parque Colúmbia ", "Pavuna ", "Penha", "Penha Circular", "Quintino Bocaiuva ", "Ramos ", "Ricardo de Albuquerque", "Rocha Miranda ", "Tomás Coelho ", "Turiaçu ", "Vaz Lobo ", "Vicente de Carvalho ", "Vigário Geral ", "Vila da Penha ", "Vila Kosmos ", "Vista Alegre "};
            Arrays.sort(a);
            return a;
        }
        if (zone.equals("Baixada")) {
            String[] a = new String[]{"Belford Roxo", "Duque de Caxias", "Guapimirim", "Itaguai", "Japeri", "Magé", "Mesquita", "Nilópolis", "Nova Iguaçu ", "Paracambi ", "Queimados ", "São João de Meriti ", "Seropédica ", };
            Arrays.sort(a);
            return a;
        }
        if (zone.equals("Grande Niterói")) {
            String[] a = new String[]{"Niterói Região oceânica", "Niterói Centro", "São Gonçalo", "Maricá", "Itaboraí", "Tanguá", "Rio Bonito"};
            Arrays.sort(a);
            return a;
        }
        return null;
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
            Date date2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
            formattedTime = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(date2);
        } catch (ParseException e) {
            e.printStackTrace();
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
}
