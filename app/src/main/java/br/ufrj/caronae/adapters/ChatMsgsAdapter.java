package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.models.ChatMessageReceived;

public class ChatMsgsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessageReceived> chatMsgsList;
    private final int color;

    public ChatMsgsAdapter(List<ChatMessageReceived> chatMsgsList, int color) {
        this.chatMsgsList = chatMsgsList;
        this.color = color;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        if (viewType == 0) {
            View contactView = inflater.inflate(R.layout.item_chatmsg_right, parent, false);
            viewHolder = new ViewHolderRight(contactView);
        } else {
            View contactView = inflater.inflate(R.layout.item_chatmsg_left, parent, false);
            viewHolder = new ViewHolderLeft(contactView);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessageReceived msg = chatMsgsList.get(position);

        if (holder.getItemViewType() == 0) {
            ViewHolderRight viewHolderRight = (ViewHolderRight) holder;

            viewHolderRight.msg_tv.setText(msg.getMessage());
            String time = "";
            try {
                time = msg.getTime().split(" ")[1].substring(0, 5);
            } catch (Exception e) {
                Log.e("ChatMsgsAdapter", e.getMessage());
            }
            viewHolderRight.time_tv.setText(time);

        } else {
            ViewHolderLeft viewHolderLeft = (ViewHolderLeft) holder;

            viewHolderLeft.msg_tv.setText(msg.getMessage());
            String time = "";
            try {
                time = msg.getTime().split(" ")[1].substring(0, 5);
            } catch (Exception e) {
                Log.e("ChatMsgsAdapter", e.getMessage());
            }
            viewHolderLeft.time_tv.setText(time);

            viewHolderLeft.sender_name_tv.setText(msg.getSenderName());
            viewHolderLeft.sender_name_tv.setTextColor(color);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageReceived msg = chatMsgsList.get(position);
        return msg.getSenderId().equals(App.getUser().getDbId() + "") ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return chatMsgsList.size();
    }

    public static class ViewHolderLeft extends RecyclerView.ViewHolder {
        public TextView msg_tv;
        public TextView sender_name_tv;
        public TextView time_tv;

        private ViewHolderLeft(View itemView) {
            super(itemView);

            msg_tv = itemView.findViewById(R.id.msg_tv);
            sender_name_tv = itemView.findViewById(R.id.sender_name_tv);
            time_tv = itemView.findViewById(R.id.time_tv);
        }
    }

    public static class ViewHolderRight extends RecyclerView.ViewHolder {
        public TextView msg_tv;
        public TextView time_tv;

        private ViewHolderRight(View itemView) {
            super(itemView);
            msg_tv = itemView.findViewById(R.id.msg_tv);
            time_tv = itemView.findViewById(R.id.time_tv);
        }
    }

    public void updateList(List<ChatMessageReceived> newList){
        chatMsgsList = newList;
    }
}
