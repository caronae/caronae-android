package br.ufrj.caronae.acts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.adapters.ChatMsgsAdapter;
import br.ufrj.caronae.comparators.ChatMsgComparator;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.ChatMessageReceivedFromJson;
import br.ufrj.caronae.models.ChatMessageSendResponse;
import br.ufrj.caronae.models.ModelReceivedFromChat;
import br.ufrj.caronae.models.NewChatMsgIndicator;
import br.ufrj.caronae.models.RideEndedEvent;
import br.ufrj.caronae.models.modelsforjson.ChatSendMessageForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatAct extends AppCompatActivity {

    @Bind(R.id.chatMsgs_rv)
    RecyclerView chatMsgs_rv;
    @Bind(R.id.send_bt)
    Button send_bt;
    @Bind(R.id.msg_et)
    EditText msg_et;
    @Bind(R.id.neighborhood_tv)
    TextView neighborhood_tv;
    @Bind(R.id.date_tv)
    TextView date_tv;
    @Bind(R.id.time_tv)
    TextView time_tv;
    @Bind(R.id.lay1)
    RelativeLayout lay1;
    @Bind(R.id.swipe_refresh_chat)
    SwipeRefreshLayout swipeRefreshLayout;

    private String rideId;
    private static List<ChatMessageReceived> chatMsgsList;
    static int color;
    Context context;
    static ChatMsgsAdapter chatMsgsAdapter;

    private final String MESSAGE_WITH_USER_BUNDLE_KEY = "message";
    private final String SENDER_ID_BUNDLE_KEY = "senderId";
    private final String SENT_TIME_BUNDLE_KEY = "google.sent_time";
    private final String RIDE_ID_BUNDLE_KEY = "rideId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        App.getBus().register(this);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        rideId = getIntent().getExtras().getString("rideId");
        List<ChatAssets> l = ChatAssets.find(ChatAssets.class, "ride_id = ?", rideId);
        if (l == null || l.isEmpty()) {
            finish();
            return;
        }

        ChatAssets chatAssets = l.get(0);

        context = this;
        color = chatAssets.getColor();
        lay1.setBackgroundColor(color);
        int bgRes = chatAssets.getBgRes();
        send_bt.setBackgroundResource(bgRes);
        String neighborhood = chatAssets.getLocation();
        neighborhood_tv.setText(neighborhood);
        String date = chatAssets.getDate();
        date_tv.setText(date);
        String time = chatAssets.getTime();
        time_tv.setText(time);

        chatMsgsList = ChatMessageReceived.find(ChatMessageReceived.class, "ride_id = ?", rideId);
        Collections.sort(chatMsgsList, new ChatMsgComparator());

        chatMsgsAdapter = new ChatMsgsAdapter(chatMsgsList, color);
        chatMsgs_rv.setAdapter(chatMsgsAdapter);
        chatMsgs_rv.setLayoutManager(new LinearLayoutManager(context));


        //TODO: Check multiples notifications
//        if (getIntent().getExtras().get(SENDER_ID_BUNDLE_KEY) != null){
//            ChatMessageReceived cmr = new ChatMessageReceived(
//                    getSenderNameFromNotification(getIntent().getExtras().getString(MESSAGE_WITH_USER_BUNDLE_KEY)),
//                    getIntent().getExtras().getString(SENDER_ID_BUNDLE_KEY),
//                    getMessageFromNotification(getIntent().getExtras().getString(MESSAGE_WITH_USER_BUNDLE_KEY)),
//                    getIntent().getExtras().getString(RIDE_ID_BUNDLE_KEY),
//                    getTimeFromNotification(getIntent().getExtras().getLong(SENT_TIME_BUNDLE_KEY)));
//
//            chatMsgsList.add(cmr);
//        }

        if (!chatMsgsList.isEmpty())
            chatMsgs_rv.scrollToPosition(chatMsgsList.size() - 1);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateMsgsListWithServer(rideId);
            }
        });

        updateMsgsListWithServer(rideId);

    }

    @OnClick(R.id.send_bt)
    public void sendBt() {
        final String message = msg_et.getText().toString();

        msg_et.setText("");

        if (message.isEmpty())
            return;

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        final ChatMessageReceived msg = new ChatMessageReceived(App.getUser().getName(), App.getUser().getDbId() + "", message, rideId, time);

        updateMsgsList(msg);

        App.getChatService(getApplicationContext()).sendChatMsg(rideId, new ChatSendMessageForJson(message))
                .enqueue(new Callback<ChatMessageSendResponse>() {
                    @Override
                    public void onResponse(Call<ChatMessageSendResponse> call, Response<ChatMessageSendResponse> response) {
                        if (response.isSuccessful()) {
                            ChatMessageSendResponse chatMessageSendResponse = response.body();
                            Log.i("Message Sent", "Sulcefully Send Chat Messages");
                            Log.d("CHAT", "mesage: " + chatMessageSendResponse.getResponseMessage() + " ID: " + chatMessageSendResponse.getMessageId() + "");
                            msg.setId(Long.parseLong(chatMessageSendResponse.getMessageId()));
                            chatMsgsList.get(chatMsgsList.size() - 1).setId(Long.parseLong(chatMessageSendResponse.getMessageId()));
                            msg.save();
                            Util.toast("Mensagem enviada");
                        } else {
                            Util.toast("Erro ao enviar mensagem de chat, verifique sua conexão");

                            chatMsgsList.remove(chatMsgsList.size() - 1);
                            chatMsgsAdapter.notifyItemRemoved(chatMsgsList.size());

                            msg_et.setText(msg.getMessage());

                            /************* Esconde o teclado ***********/

                            View view = ChatAct.this.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            /*********/
                            Log.e("SendMessages", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatMessageSendResponse> call, Throwable t) {
                        Util.toast("Erro ao enviar mensagem de chat, verifique sua conexão");

                        chatMsgsList.remove(chatMsgsList.size() - 1);
                        chatMsgsAdapter.notifyItemRemoved(chatMsgsList.size());

                        msg_et.setText(msg.getMessage());

                        /************* Esconde o teclado ***********/

                        View view = ChatAct.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        /*********/
                        Log.e("SendMessages", t.getMessage());
                    }
                });
    }


    @Subscribe
    public void updateMsgsList(ChatMessageReceived msg) {

        Log.i("GetMessages", "Updatando");

        chatMsgsList.add(msg);

//        msg.save();

        ChatMsgsAdapter adapter = (ChatMsgsAdapter) chatMsgs_rv.getAdapter();
        adapter.notifyItemInserted(chatMsgsList.size() - 1);

        chatMsgs_rv.scrollToPosition(chatMsgsList.size() - 1);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void updateMsgsList() {

        chatMsgsAdapter = new ChatMsgsAdapter(chatMsgsList, color);
        chatMsgs_rv.setAdapter(chatMsgsAdapter);
        chatMsgs_rv.setLayoutManager(new LinearLayoutManager(context));
        if (!chatMsgsList.isEmpty())
            chatMsgs_rv.scrollToPosition(chatMsgsList.size() - 1);

        swipeRefreshLayout.setRefreshing(false);

    }

    @Subscribe
    public void updateMsgsListWithServer(final String rideId) {

        String since = null;
        if (chatMsgsList.size() != 0) {
            boolean lastMessageisMine = true;
            int counter = chatMsgsList.size() - 1;
            while (lastMessageisMine && counter >= 0) {
                if (!chatMsgsList.get(counter).getSenderId().equals(String.valueOf(App.getUser().getDbId()))) {
                    since = chatMsgsList.get(counter).getTime();
                    lastMessageisMine = false;
                }
                counter--;
            }
        }

        /************************************************************/
        App.getChatService(getApplicationContext()).requestChatMsgs(rideId, since)
                .enqueue(new Callback<ModelReceivedFromChat>() {
                    @Override
                    public void onResponse(Call<ModelReceivedFromChat> call, Response<ModelReceivedFromChat> response) {
                        if (response.isSuccessful()) {
                            ModelReceivedFromChat chatMessagesReceived = response.body();
                            if (chatMessagesReceived != null && chatMessagesReceived.getMessages().size() != 0) {

                                List<ChatMessageReceivedFromJson> listMessages = chatMessagesReceived.getMessages();
                                for (int mensagesNum = 0; mensagesNum < listMessages.size(); mensagesNum++) {
                                    ChatMessageReceived cmr = new ChatMessageReceived(listMessages.get(mensagesNum).getUser().getName(),
                                            String.valueOf(listMessages.get(mensagesNum).getUser().getId()),
                                            listMessages.get(mensagesNum).getMessage(),
                                            rideId,
                                            listMessages.get(mensagesNum).getTime());
                                    cmr.setId(Long.parseLong(listMessages.get(mensagesNum).getMessageId()));
//                        chatMsgsList.add(cmr);

                                    if (!messageAlrealdyExist(Long.parseLong(listMessages.get(mensagesNum).getMessageId()))) {
                                        cmr.save();
                                        updateMsgsList(cmr);
                                    }
                                }
                            }

                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        } else {
                            Util.toast("Erro ao Recuperar mensagem de chat");
                            Log.e("GetMessages", response.message());
                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelReceivedFromChat> call, Throwable t) {
                        Util.toast("Erro ao Recuperar mensagem de chat");
                        Log.e("GetMessages", t.getMessage());
                        swipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
        /************************************************************/

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private boolean messageAlrealdyExist(long messageId) {
        boolean messageAlreadyExist = false;
        int counter = chatMsgsList.size() - 1;
        while (!messageAlreadyExist && counter >= 0) {
            if (chatMsgsList.get(counter).getId() == messageId)
                messageAlreadyExist = true;
            counter--;
        }
        return messageAlreadyExist;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        NewChatMsgIndicator.deleteAll(NewChatMsgIndicator.class, "db_id = ?", rideId);
    }

    @Subscribe
    public void rideEndedEvent(RideEndedEvent rideEndedEvent) {
        Log.i("rideEndedEvent", "chatact" + rideEndedEvent.getRideId());

        if (rideId.equals(rideEndedEvent.getRideId())) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            App.getBus().unregister(this);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        NewChatMsgIndicator.deleteAll(NewChatMsgIndicator.class, "db_id = ?", rideId);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPref.setChatActIsForeground(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPref.setChatActIsForeground(false);
    }
}
