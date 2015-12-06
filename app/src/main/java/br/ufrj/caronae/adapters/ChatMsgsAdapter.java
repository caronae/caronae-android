package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.ufrj.caronae.R;
import br.ufrj.caronae.models.ChatMessageReceived;

public class ChatMsgsAdapter extends RecyclerView.Adapter<ChatMsgsAdapter.ViewHolder> {

    private final List<ChatMessageReceived> chatMsgsList;
    private final Context context;
    private final int color;

    public ChatMsgsAdapter(List<ChatMessageReceived> chatMsgsList, int color, Context context) {
        this.chatMsgsList = chatMsgsList;
        this.context = context;
        this.color = color;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_chatmsg, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessageReceived msg = chatMsgsList.get(position);
        holder.msg_tv.setText(msg.getMessage());
        holder.time_tv.setText(msg.getTime());

        holder.sender_name_tv.setText(msg.getSenderName());
        holder.sender_name_tv.setTextColor(color);

        /*if (msg.getSenderId().equals(App.getUser().getDbId()+"")) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.card_view.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.card_view.setLayoutParams(layoutParams);

            holder.sender_name_tv.setVisibility(View.GONE);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.card_view.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.card_view.setLayoutParams(layoutParams);

            holder.sender_name_tv.setVisibility(View.VISIBLE);
            holder.sender_name_tv.setText(msg.getSenderName());
            holder.sender_name_tv.setTextColor(color);
        }*/
    }

    @Override
    public int getItemCount() {
        return chatMsgsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView msg_tv;
        public TextView sender_name_tv;
        public TextView time_tv;
        public CardView card_view;

        public ViewHolder(View itemView) {
            super(itemView);

            msg_tv = (TextView) itemView.findViewById(R.id.msg_tv);
            sender_name_tv = (TextView) itemView.findViewById(R.id.sender_name_tv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            card_view = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
