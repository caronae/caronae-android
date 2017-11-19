package br.ufrj.caronae.acts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import br.ufrj.caronae.firebase.FetchReceivedMessagesService;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.ChatMessageSendResponse;
import br.ufrj.caronae.models.NewChatMsgIndicator;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideEndedEvent;
import br.ufrj.caronae.models.modelsforjson.ChatSendMessageForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatAct extends AppCompatActivity {

    @BindView(R.id.chatMsgs_rv)
    RecyclerView chatMsgs_rv;
    @BindView(R.id.send_bt)
    com.github.clans.fab.FloatingActionButton send_bt;
    @BindView(R.id.msg_et)
    EditText msg_et;
    @BindView(R.id.chat_header_text)
    TextView headerText;
    @BindView(R.id.lay1)
    RelativeLayout lay1;
    @BindView(R.id.card_loading_menssages_sign)
    CardView cardLoadingMessages;
    @BindView(R.id.loading_message_text)
    TextView loadMessageText;

    int textCounter = 0;


    private String rideId;
    private static List<ChatMessageReceived> chatMsgsList;
    static int color;
    Context context;
    static ChatMsgsAdapter chatMsgsAdapter;
    Toolbar toolbar;

    Animation translate;

    private final String BROADCAST_NEW_MESSAGES_NULL = "messagesNull";
    private final String RIDE_ID_BUNDLE_KEY = "rideId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        App.getBus().register(this);

        SharedPref.setChatActIsForeground(true);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        rideId = getIntent().getExtras().getString(RIDE_ID_BUNDLE_KEY);
//        ChatAssets chatAssets;
        List<ChatAssets> l = ChatAssets.find(ChatAssets.class, "ride_id = ?", rideId);
        if (l == null || l.isEmpty()) {
            List<Ride> ride = Ride.find(Ride.class, "db_id = ?", rideId);
            if (ride == null || ride.isEmpty()){
                getRideFromServer(this);
            }
        } else {
            configureActivityWithChatAssets(l.get(0));
        }

    }

    private void configureActivityWithChatAssets(ChatAssets chatAssets) {
        context = this;
        color = chatAssets.getColor();
        int colorPressed = Util.getPressedColorbyNormalColor(color);
        lay1.setBackgroundColor(color);
        toolbar.setBackgroundColor(color);
        int bgRes = chatAssets.getBgRes();
//        send_bt.setBackgroundResource(bgRes);
        send_bt.setColorNormal(color);
        send_bt.setColorPressed(colorPressed);
        String neighborhood = chatAssets.getLocation();
//        neighborhood_tv.setText(neighborhood);
        String date = chatAssets.getDate();
//        date_tv.setText(date);
        String time = chatAssets.getTime();
//        time_tv.setText(time);
        headerText.setText(neighborhood + " - " + date + " - " + time);

        chatMsgsList = ChatMessageReceived.find(ChatMessageReceived.class, "ride_id = ?", rideId);
        Collections.sort(chatMsgsList, new ChatMsgComparator());

        chatMsgsAdapter = new ChatMsgsAdapter(chatMsgsList, color);
        chatMsgs_rv.setAdapter(chatMsgsAdapter);
        chatMsgs_rv.setLayoutManager(new LinearLayoutManager(context));

        chatMsgsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Util.toast("mudou");
            }
        });


        if (!chatMsgsList.isEmpty())
            chatMsgs_rv.scrollToPosition(chatMsgsList.size() - 1);

    }

    @OnClick(R.id.send_bt)
    public void sendBt() {
        final String message = msg_et.getText().toString();

        msg_et.setText("");

        if (message.isEmpty())
            return;

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        final ChatMessageReceived msg = new ChatMessageReceived(App.getUser().getName(), App.getUser().getDbId() + "", message, rideId, time);
        msg.setId((long) -1);

        chatMsgsList.add(msg);

        final ChatMsgsAdapter adapter = (ChatMsgsAdapter) chatMsgs_rv.getAdapter();
        adapter.notifyItemInserted(chatMsgsList.size() - 1);

        chatMsgs_rv.scrollToPosition(chatMsgsList.size() - 1);

        App.getChatService(getApplicationContext()).sendChatMsg(rideId, new ChatSendMessageForJson(message))
                .enqueue(new Callback<ChatMessageSendResponse>() {
                    @Override
                    public void onResponse(Call<ChatMessageSendResponse> call, Response<ChatMessageSendResponse> response) {
                        if (response.isSuccessful()) {
                            ChatMessageSendResponse chatMessageSendResponse = response.body();
                            Log.i("Message Sent", "Sulcefully Send Chat Messages");
                            msg.setId(Long.parseLong(chatMessageSendResponse.getMessageId()));
                            chatMsgsList.get(getMessagePositionWithId(chatMsgsList, msg.getId())).setId(Long.parseLong(chatMessageSendResponse.getMessageId()));
                            msg.save();
                        } else {
                            Util.treatResponseFromServer(response);
                            Util.toast("Erro ao enviar mensagem de chat, verifique sua conexao");

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
                        Util.toast("Erro ao enviar mensagem de chat, verifique sua conexao");

                        if (chatMsgsList.size() > 0) {
                            chatMsgsList.remove(chatMsgsList.size() - 1);
                            chatMsgsAdapter.notifyItemRemoved(chatMsgsList.size());

                            msg_et.setText(msg.getMessage());
                        }

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

        List<ChatMessageReceived> savedChatMsgsList = new ArrayList<>();

        savedChatMsgsList = ChatMessageReceived.find(ChatMessageReceived.class, "ride_id = ?", rideId);

        if (savedChatMsgsList.size() != chatMsgsList.size()){
            chatMsgsList = savedChatMsgsList;
            chatMsgsAdapter.updateList(chatMsgsList);
            chatMsgsAdapter.notifyItemRangeInserted(chatMsgsAdapter.getItemCount(), chatMsgsList.size() - chatMsgsAdapter.getItemCount());
            chatMsgs_rv.scrollToPosition(chatMsgsList.size() - 1);
        }

        if (translate.hasEnded()) {
            translate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_loading_messages_up);
            cardLoadingMessages.startAnimation(translate);
        } else {
            translate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    translate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_loading_messages_up);
                    cardLoadingMessages.startAnimation(translate);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    public void updateLoadText(ArrayList<String> textToLoad) {
        loadMessageText.setText(textToLoad.get(textCounter));
        textCounter++;
        if (textCounter >= 3) {
            textCounter = 0;
        }
    }

    @Subscribe
    public void updateMsgsListWithServer(final String rideId) {

        translate = AnimationUtils.loadAnimation(this, R.anim.anim_loading_messages_down);
        cardLoadingMessages.startAnimation(translate);


        String since = null;
        if (chatMsgsList == null){
            chatMsgsList = new ArrayList<>();
        }
        if (chatMsgsList.size() != 0) {
            boolean lastMessageIsMine = true;
            int counter = chatMsgsList.size() - 1;
            while (lastMessageIsMine && counter >= 0) {
                if (!chatMsgsList.get(counter).getSenderId().equals(String.valueOf(App.getUser().getDbId()))) {
                    since = chatMsgsList.get(counter).getTime();
                    lastMessageIsMine = false;
                }
                counter--;
            }
        }

        Intent fetchMessageService = new Intent(getApplicationContext(), FetchReceivedMessagesService.class);
        fetchMessageService.putExtra(RIDE_ID_BUNDLE_KEY, rideId);
        fetchMessageService.putExtra("since", since);
        getApplicationContext().startService(fetchMessageService);
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
        } catch (IllegalArgumentException e) {
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

        updateMsgsListWithServer(rideId);

    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPref.setChatActIsForeground(false);
    }

    private int getMessagePositionWithId(List<ChatMessageReceived> chatMsgsList, long id) {
        for (int position = chatMsgsList.size() - 1; position >= 0; position--) {
            if (chatMsgsList.get(position).getId() == id)
                return position;
        }
        return -1;
    }

    private class ReceiveBroadcastNewMessagesNull extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (translate.hasEnded()) {
                translate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_loading_messages_up);
                cardLoadingMessages.startAnimation(translate);
            } else {
                translate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        translate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_loading_messages_up);
                        cardLoadingMessages.startAnimation(translate);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }

        }
    }

    private Ride getRideFromServer(final Activity activity) {
        final RideForJson[] ride = {null};
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ProgressDialog pd = ProgressDialog.show(activity, "", activity.getString(R.string.wait), true, true);
                App.getNetworkService(activity).getMyActiveRides()
                        .enqueue(new Callback<List<RideForJson>>() {
                            @Override
                            public void onResponse(Call<List<RideForJson>> call, Response<List<RideForJson>> response) {

                                if (response.isSuccessful()) {
                                    List<RideForJson> rideWithUsersList = response.body();

                                    Log.e("RIDE", "rides encontradas: " + rideWithUsersList.size());

                                    if (rideWithUsersList == null || rideWithUsersList.isEmpty()) {
                                        pd.dismiss();
                                        return;
                                    }

                                    for (int rideIndex = 0; rideIndex < rideWithUsersList.size(); rideIndex++){
                                        if ((rideWithUsersList.get(rideIndex).getId() + "").equals(rideId)){
                                            ride[0] = rideWithUsersList.get(rideIndex);
                                        }
                                    }
                                    if (ride[0] != null){
                                        String location;
                                        if (ride[0].isGoing())
                                            location = ride[0].getNeighborhood() + " -> " + ride[0].getHub();
                                        else
                                            location = ride[0].getHub() + " -> " + ride[0].getNeighborhood();
                                        ChatAssets chatAssets = new ChatAssets(rideId, location,
                                                Util.getColorbyZone(ride[0].getZone()),
                                                Util.getBgResByZone(ride[0].getZone()),
                                                Util.formatBadDateWithoutYear(ride[0].getDate()),
                                                Util.formatTime(ride[0].getTime()));
                                        chatAssets.save();
                                        configureActivityWithChatAssets(chatAssets);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), MainAct.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    pd.dismiss();
                                } else {
                                    Util.treatResponseFromServer(response);
                                    pd.dismiss();

                                    Log.e("getMyActiveRides", response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<List<RideForJson>> call, Throwable t) {
                                pd.dismiss();
                                Log.e("getMyActiveRides", t.getMessage());
                            }
                        });
            }
        });
        return ride[0];
    }
}
