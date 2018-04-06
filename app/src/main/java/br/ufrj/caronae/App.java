package br.ufrj.caronae;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.orm.SugarApp;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import br.ufrj.caronae.ACRAreport.CrashReportFactory;
import br.ufrj.caronae.acts.LoginAct;
import br.ufrj.caronae.asyncs.LogOut;
import br.ufrj.caronae.models.User;


/** Usa o Falae para reportar crashes **/
@ReportsCrashes(
        reportSenderFactoryClasses = {CrashReportFactory.class},
        mode = ReportingInteractionMode.NOTIFICATION,
        resNotifText = R.string.crash_notifcation_text,
        resNotifTitle = R.string.crash_notifcation_title,
        resNotifTickerText = R.string.crash_notifcation_ticker_text,
        resDialogText = R.string.crash_notifcation_text
)
/******/

public class App extends SugarApp {


    private static App inst;
    private static User user;
    private static MainThreadBus bus;

    public App() {
        inst = this;
    }

    public static App getInst() {
        return inst;
    }

    public static boolean isUserLoggedIn() {
        return getUser() != null;
    }

    public static void clearUserVar() {
        user = null;
    }

    public static User getUser() {
        if (user == null) {
            String userJson = SharedPref.getUserPref();
            if (!userJson.equals(SharedPref.MISSING_PREF))
                user = new Gson().fromJson(userJson, User.class);
        }

        return user;
    }

    public static MainThreadBus getBus() {
        if (bus == null) {
            bus = new MainThreadBus();
        }

        return bus;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
        MultiDex.install(base);
    }

    public static void LogOut(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(SharedPref.TOPIC_GERAL);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(App.getUser().getDbId() + "");
        new LogOut(App.getInst()).execute();
        Intent intent = new Intent(App.getInst(), LoginAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getInst().startActivity(intent);
    }
}
