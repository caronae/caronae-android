package br.ufrj.caronae.acts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.otto.Subscribe;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.adapters.ChatMsgsAdapter;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.modelsforjson.ChatMessageSent;
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
    List<ChatMessageReceived> chatMsgsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        rideId = getIntent().getExtras().getString("rideId");

        chatMsgsList = ChatMessageReceived.find(ChatMessageReceived.class, "ride_id = ?", rideId);
        chatMsgs_rv.setAdapter(new ChatMsgsAdapter(chatMsgsList, this));
        chatMsgs_rv.setLayoutManager(new LinearLayoutManager(this));

        if (!chatMsgsList.isEmpty())
            chatMsgs_rv.scrollToPosition(chatMsgsList.size() - 1);

        App.getBus().register(this);
    }

    @OnClick(R.id.send_bt)
    public void sendBt() {
        final String message = msg_et.getText().toString();
        msg_et.setText("");
        App.getChatService().sendChatMsg(new ChatMessageSent(rideId, message), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i("sendChatMsg", msg_et.getText().toString());
                updateMsgsList(new ChatMessageReceived(App.getUser().getName(), App.getUser().getDbId()+"", message, rideId));
            }

            @Override
            public void failure(RetrofitError error) {
                App.toast("Erro ao enviar mensagem de chat");
                Log.e("sendChatMsg", error.getMessage());
            }
        });
    }

    @Subscribe
    public void updateMsgsList(ChatMessageReceived msg) {
        Log.i("updateMsgsList", msg.getMessage());

        chatMsgsList.add(msg);

        ChatMsgsAdapter adapter = (ChatMsgsAdapter) chatMsgs_rv.getAdapter();
        adapter.notifyItemInserted(chatMsgsList.size()-1);

        chatMsgs_rv.scrollToPosition(chatMsgsList.size() - 1);
    }
}
