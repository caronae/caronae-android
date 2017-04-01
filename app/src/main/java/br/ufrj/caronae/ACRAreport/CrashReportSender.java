package br.ufrj.caronae.ACRAreport;

import android.content.Context;
import android.support.annotation.NonNull;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import br.ufrj.caronae.App;
import br.ufrj.caronae.Util;

import static br.ufrj.caronae.Util.saveMessageToSharedPref;

/**
 * Created by Luis-DELL on 3/18/2017.
 */

public class CrashReportSender implements ReportSender {
    @Override
    public void send(@NonNull final Context context, @NonNull CrashReportData errorContent) throws ReportSenderException {
        String name = "Não logado";
        String email = "Não logado";

        if (App.isUserLoggedIn()){
            name = App.getUser().getName();
            email = App.getUser().getEmail();
        }

        String message = "Android: "
                + errorContent.getProperty(ReportField.ANDROID_VERSION)
                + "\n"
                + "Versão do app: "
                + errorContent.getProperty(ReportField.APP_VERSION_NAME)
                + "\n"
                + "Nome: "
                + name
                + "\n"
                + "Email: "
                + email
                + "\n\n"
                + "StackTrace: \n"
                + errorContent.getProperty(ReportField.STACK_TRACE)
                + "\n"
                + "App Log: "
                + errorContent.getProperty(ReportField.APPLICATION_LOG);

        saveMessageToSharedPref(context, message);

        Util.sendCrashReport(context, message);
    }
}
