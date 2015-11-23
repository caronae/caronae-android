package br.ufrj.caronae;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import retrofit.client.Response;

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

        // To Collapse
        //

        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator(0.5f));
        v.startAnimation(anim);
    }

    public static String getResponseBody(Response response) {
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
            String[] a = new String[]{"São Cristóvão", "Benfica", "Caju", "Catumbi", "Centro",
                    "Cidade Nova", "Estácio", "Gamboa", "Glória", "Lapa", "Mangueira", "Paquetá",
                    "Rio Comprido", "Santa Teresa", "Santo Cristo", "Saúde", "Vasco da Gama"};
            Arrays.sort(a);
            return a;
        }
        if (zone.equals("Zona Sul")) {
            String[] a = new String[]{"Botafogo", "Catete", "Copacabana", "Cosme Velho",
                    "Flamengo", "Gávea", "Humaitá", "Ipanema", "Jardim Botânico", "Lagoa",
                    "Laranjeiras", "Leblon", "Leme", "Rocinha", "São Conrado", "Urca", "Vidigal"};
            Arrays.sort(a);
            return a;
        }
        if (zone.equals("Zona Oeste")) {
            String[] a = new String[]{"Anil", "Barra da Tijuca", "Camorim", "Cidade de Deus",
                    "Curicica", "Freguesia de Jacarepaguá", "Gardênia Azul", "Grumari",
                    "Itanhangá", "Jacarepaguá", "Joá", "Praça Seca ", "Pechincha",
                    "Recreio dos Bandeirantes", "Tanque", "Taquara", "Vargem Grande",
                    "Vargem Pequena", "Vila Valqueire", "Bangu", "Deodoro", "Gericinó",
                    "Jardim Sulacap", "Magalhães Bastos", "Padre Miguel", "Realengo",
                    "Santíssimo", "Senador Camará", "Vila Militar", "Barra de Guaratiba",
                    "Campo Grande", "Cosmos", "Guaratiba", "Inhoaíba", "Paciência",
                    "Pedra de Guaratiba", "Santa Cruz", "Senador Vasconcelos", "Sepetiba"};
            Arrays.sort(a);
            return a;
        }
        if (zone.equals("Zona Norte")) {
            String[] a = new String[]{"Alto da Boa Vista", "Andaraí", "Grajaú", "Maracanã",
                    "Praça da Bandeira", "Tijuca", "Vila Isabel", "Abolição", "Água Santa",
                    "Cachambi", "Del Castilho", "Encantado", "Engenho de Dentro", "Engenho Novo",
                    "Inhaúma", "Jacaré", "Jacarezinho", "Lins de Vasconcelos",
                    "Maria da Graça", "Méier", "Piedade", "Pilares", "Riachuelo", "Rocha",
                    "Sampaio", "São Francisco Xavier", "Todos os Santos", "Bancários",
                    "Cacuia", "Cidade Universitária", "Cocotá",
                    "Freguesia (Ilha do Governador)", "Galeão", "Jardim Carioca",
                    "Jardim Guanabara", "Maré", "Monero", "Pitangueiras", "Portuguesa",
                    "Praia da Bandeira", "Ribeira", "Tauá", "Zumbi", "Acari", "Anchieta",
                    "Barros Filho", "Bento Ribeiro", "Brás de Pina", "Bonsucesso", "Campinho",
                    "Cavalcanti", "Cascadura", "Coelho Neto", "Colégio", "Complexo do Alemão",
                    "Cordovil", "Costa Barros", "Engenheiro Leal", "Engenho da Rainha",
                    "Guadalupe", "Higienópolis", "Honório Gurgel", "Irajá", "Jardim América",
                    "Madureira", "Marechal Hermes", "Manguinhos", "Oswaldo Cruz", "Olaria",
                    "Parada de Lucas", "Parque Colúmbia", "Pavuna", "Penha", "Penha Circular",
                    "Quintino Bocaiuva", "Ramos", "Ricardo de Albuquerque", "Rocha Miranda",
                    "Tomás Coelho", "Turiaçu", "Vaz Lobo", "Vicente de Carvalho",
                    "Vigário Geral", "Vila da Penha", "Vila Kosmos", "Vista Alegre"};
            Arrays.sort(a);
            return a;
        }
        if (zone.equals("Baixada")) {
            String[] a = new String[]{"Belford Roxo", "Duque de Caxias", "Guapimirim", "Itaguai",
                    "Japeri", "Magé", "Mesquita", "Nilópolis", "Nova Iguaçu", "Paracambi",
                    "Queimados", "São João de Meriti", "Seropédica",};
            Arrays.sort(a);
            return a;
        }
        if (zone.equals("Grande Niterói")) {
            String[] a = new String[]{"Região oceânica", "Centro", "São Gonçalo",
                    "Maricá", "Itaboraí", "Tanguá", "Rio Bonito"};
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
