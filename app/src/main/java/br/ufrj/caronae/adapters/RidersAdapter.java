package br.ufrj.caronae.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.models.User;

public class RidersAdapter extends RecyclerView.Adapter<RidersAdapter.ViewHolder> {

    private final List<User> users;
    private final MainAct activity;

    public RidersAdapter(List<User> users, MainAct activity) {
        this.users = users;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_riders, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.name_tv.setText(user.getName());
        holder.name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(activity).
                        setTitle(user.getName()).
                        setMessage(user.getProfile() + "\n" + user.getCourse() + "\n" + user.getPhoneNumber() + "\nUsuário desde " + user.getCreatedAt().split(" ")[0]).
                        show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name_tv;

        public ViewHolder(View itemView) {
            super(itemView);

            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
        }
    }
}
