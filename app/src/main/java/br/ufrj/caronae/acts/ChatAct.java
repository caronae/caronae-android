package br.ufrj.caronae.acts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.adapters.ChatMsgsAdapter;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.RideEndedEvent;
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
    @Bind(R.id.neighborhood_tv)
    TextView neighborhood_tv;
    @Bind(R.id.riders_tv)
    TextView riders_tv;
    @Bind(R.id.date_tv)
    TextView date_tv;
    @Bind(R.id.time_tv)
    TextView time_tv;
    @Bind(R.id.lay1)
    RelativeLayout lay1;

    private String rideId;
    private List<ChatMessageReceived> chatMsgsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        int color = getIntent().getExtras().getInt("color");
        lay1.setBackgroundColor(color);
        int bgRes = getIntent().getExtras().getInt("bgRes");
        send_bt.setBackgroundResource(bgRes);
        String neighborhood = getIntent().getExtras().getString("location");
        neighborhood_tv.setText(neighborhood);
        String riders = getIntent().getExtras().getString("riders");
        riders_tv.setText(riders);
        String date = getIntent().getExtras().getString("date");
        date_tv.setText(date);
        String time = getIntent().getExtras().getString("time");
        time_tv.setText(time);

        rideId = getIntent().getExtras().getString("rideId");
        chatMsgsList = ChatMessageReceived.find(ChatMessageReceived.class, "ride_id = ?", rideId);

        chatMsgs_rv.setAdapter(new ChatMsgsAdapter(chatMsgsList, color));
        chatMsgs_rv.setLayoutManager(new LinearLayoutManager(this));

        if (!chatMsgsList.isEmpty())
            chatMsgs_rv.scrollToPosition(chatMsgsList.size() - 1);

        App.getBus().register(this);
    }

    @OnClick(R.id.send_bt)
    public void sendBt() {
        final String message = msg_et.getText().toString();
        if (message.isEmpty())
            return;
        msg_et.setText("");

        String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
        updateMsgsList(new ChatMessageReceived(App.getUser().getName(), App.getUser().getDbId() + "", message, rideId, time));

        App.getChatService().sendChatMsg(new ChatMessageSent(rideId, message, time), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i("sendChatMsg", msg_et.getText().toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Util.toast("Erro ao enviar mensagem de chat");
                Log.e("sendChatMsg", error.getMessage());
            }
        });
    }

    @Subscribe
    public void updateMsgsList(ChatMessageReceived msg) {
        Log.i("updateMsgsList", msg.getMessage());

        chatMsgsList.add(msg);

        ChatMsgsAdapter adapter = (ChatMsgsAdapter) chatMsgs_rv.getAdapter();
        adapter.notifyItemInserted(chatMsgsList.size() - 1);

        chatMsgs_rv.scrollToPosition(chatMsgsList.size() - 1);
    }

    @Subscribe
    public void rideEndedEvent(RideEndedEvent rideEndedEvent) {
        Log.i("rideEndedEvent", "chatact" + rideEndedEvent.getRideId());

        if (rideId.equals(rideEndedEvent.getRideId())) {
            finish();
        }
    }
}
