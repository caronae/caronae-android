package br.ufrj.caronae.ACRAreport;

import android.content.Context;
import android.support.annotation.NonNull;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import br.ufrj.caronae.App;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.modelsforjson.FalaeMsgForJson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        String subject = "ANDROID CRASH REPORT";

        CaronaeAPI.service().falaeSendMessage(new FalaeMsgForJson(subject, message))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
    }
}
