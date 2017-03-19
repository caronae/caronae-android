package br.ufrj.caronae.ACRAreport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.config.ACRAConfiguration;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.acra.sender.ReportSenderFactory;

import br.ufrj.caronae.App;
import br.ufrj.caronae.models.modelsforjson.FalaeMsgForJson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Luis-DELL on 3/18/2017.
 */

public class CrashReportSender implements ReportSender {
    @Override
    public void send(@NonNull Context context, @NonNull CrashReportData errorContent) throws ReportSenderException {
        Log.e("SENDER", "VEIO SENDER");
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

        String subject = "ANDROID CRASH REPORT";

        App.getNetworkService(context).falaeSendMessage(new FalaeMsgForJson(subject, message))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.e("SENDER", "sucesso: " + response.message());
                        } else {
                            Log.e("SENDER", "falha: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("SENDER", "falha: " + t.getMessage());
                    }
                });
    }
}
