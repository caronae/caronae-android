package br.ufrj.caronae.adapters;

import android.content.Context;
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

    public ChatMsgsAdapter(List<ChatMessageReceived> chatMsgsList, Context context) {
        this.chatMsgsList = chatMsgsList;
        this.context = context;
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
        holder.msg_tv.setText(msg.getSenderName() + ": " + msg.getMessage());
    }

    @Override
    public int getItemCount() {
        return chatMsgsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView msg_tv;

        public ViewHolder(View itemView) {
            super(itemView);

            msg_tv = (TextView) itemView.findViewById(R.id.msg_tv);
        }
    }
}
