package br.ufrj.caronae;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.orm.SugarApp;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.Timer;
import java.util.TimerTask;

import br.ufrj.caronae.ACRAreport.CrashReportFactory;
import br.ufrj.caronae.acts.LoginAct;
import br.ufrj.caronae.asyncs.LogOut;
import br.ufrj.caronae.data.MainThreadBus;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.httpapis.LoginService;
import br.ufrj.caronae.httpapis.ServiceCallback;
import br.ufrj.caronae.models.User;


/** Usa o Falae para reportar crashes **/
@ReportsCrashes(
        reportSenderFactoryClasses = {CrashReportFactory.class},
        mode = ReportingInteractionMode.NOTIFICATION,
        resNotifText = R.string.crash_notifcation_message,
        resNotifTitle = R.string.crash_notifcation_title,
        resNotifTickerText = R.string.crash_notifcation_ticker_message,
        resDialogText = R.string.crash_notifcation_message
)
/******/

public class App extends SugarApp {


    private static App inst;
    private static User user;
    private static MainThreadBus bus;

    public App() {
        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                SharedPref.lastMyRidesUpdate += 1;
                SharedPref.lastSearchRidesUpdate += 1;
            }
        };
        timer.schedule (hourlyTask, 0, 1000);
        inst = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (isUserLoggedIn()) {
            checkIfUserNeedsToMigrateToJWT();
        }
    }

    private void checkIfUserNeedsToMigrateToJWT() {
        if (SharedPref.getUserJWTToken() != null) {
            Log.i("Login", "User is already using JWT.");
            return;
        }

        Log.i("Login", "User needs to migrate to JWT.");
        LoginService.service().migrateToJWT(new ServiceCallback() {
            @Override
            public void success(Object obj) {
                Log.i("Login", "User successfully migrated to JWT.");
            }

            @Override
            public void fail(Throwable t) {
                Log.e("Login", "Failed to migrate user to JWT.");
            }
        });
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
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(SharedPref.TOPIC_GERAL);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(App.getUser().getDbId() + "");
            new LogOut(App.getInst()).execute();
            Intent intent = new Intent(App.getInst(), LoginAct.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInst().startActivity(intent);
        }catch (Exception e){
            Log.e("LogOut ERROR: ", e + "");
        }
    }
}
