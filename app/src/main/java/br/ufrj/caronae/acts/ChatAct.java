package br.ufrj.caronae.acts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.adapters.ChatMsgsAdapter;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.modelsforjson.ChatMessageToSend;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChatAct extends AppCompatActivity {

    @Bind(R.id.chatMsgs_rv)
    RecyclerView chatMsgs_rv;
    @Bind(R.id.send_bt)
    Button send_bt;
    @Bind(R.id.msg_et)
    EditText msg_et;

    String rideId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        rideId = getIntent().getExtras().getString("rideId");
        //Log.i("ChatAct", "rideId = " + rideId);

        List<ChatMessageReceived> chatMsgsList = ChatMessageReceived.find(ChatMessageReceived.class, "ride_id = ?", rideId);
        /*if (chatMsgsList == null || chatMsgsList.isEmpty())
            return;*/
        chatMsgs_rv.setAdapter(new ChatMsgsAdapter(chatMsgsList, this));
        //chatMsgs_rv.setHasFixedSize(true);
        chatMsgs_rv.setLayoutManager(new LinearLayoutManager(this));

    }

    @OnClick(R.id.send_bt)
    public void sendBt() {
        App.getChatService().sendChatMsg(new ChatMessageToSend(rideId, msg_et.getText().toString()), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i("sendChatMsg", msg_et.getText().toString());
            }

            @Override
            public void failure(RetrofitError error) {
                App.toast("Erro ao enviar mensagem de chat");
                Log.e("sendChatMsg", error.getMessage());
            }
        });
    }
}
