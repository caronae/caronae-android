package br.ufrj.caronae.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.acts.ProfileAct;
import br.ufrj.caronae.models.User;

public class RidersAdapter extends RecyclerView.Adapter<RidersAdapter.ViewHolder> {

    private final List<User> users;
    private final AppCompatActivity activity;

    public RidersAdapter(List<User> users, AppCompatActivity activity) {
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

        String profilePicUrl = user.getProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty())
            Picasso.with(activity).load(profilePicUrl)
                .placeholder(R.drawable.user_pic)
                .error(R.drawable.user_pic)
                .transform(new RoundedTransformation(0))
                .into(holder.photo_iv);
        holder.name_tv.setText(user.getName().split(" ")[0]);

        if (user.getDbId() != App.getUser().getDbId()) {//dont allow user to open own profile
            holder.photo_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ProfileAct.class);
                    intent.putExtra("user", new Gson().toJson(user));
                    intent.putExtra("from", "riders");
                    activity.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo_iv;
        public TextView name_tv;

        public ViewHolder(View itemView) {
            super(itemView);

            photo_iv = (ImageView) itemView.findViewById(R.id.photo_iv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
        }
    }
}
