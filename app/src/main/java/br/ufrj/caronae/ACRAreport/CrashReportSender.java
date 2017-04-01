package br.ufrj.caronae.ACRAreport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.config.ACRAConfiguration;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.acra.sender.ReportSenderFactory;

import br.ufrj.caronae.App;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.models.modelsforjson.FalaeMsgForJson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.ufrj.caronae.Util.saveMessageToSharedPref;

/**
 * Created by Luis-DELL on 3/18/2017.
 */

public class CrashReportSender implements ReportSender {
    @Override
    public void send(@NonNull final Context context, @NonNull CrashReportData errorContent) throws ReportSenderException {
        String message = "Android: "
                + errorContent.getProperty(ReportField.ANDROID_VERSION)
                + "\n"
                + "Versão do app: "
                + errorContent.getProperty(ReportField.APP_VERSION_NAME)
                + "\n"
                + "Email: "
                + errorContent.getProperty(ReportField.USER_EMAIL)
                + "\n"
                + "StackTrace: \n"
                + errorContent.getProperty(ReportField.STACK_TRACE)
                + "\n"
                + "App Log: "
                + errorContent.getProperty(ReportField.APPLICATION_LOG);

        saveMessageToSharedPref(context, message);

        Util.sendCrashReport(context, message);
    }
}
