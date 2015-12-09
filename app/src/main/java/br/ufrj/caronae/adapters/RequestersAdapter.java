package br.ufrj.caronae.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.ProfileAct;
import br.ufrj.caronae.asyncs.CheckSubGcmTopic;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.JoinRequestIDsForJson;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestersAdapter extends RecyclerView.Adapter<RequestersAdapter.ViewHolder> {
    private final ArrayList<User> users;
    private final int rideId;
    private final Context activity;
    private final int color;

    public RequestersAdapter(ArrayList<User> users, int rideId, int color, Context activity) {
        this.users = users;
        this.rideId = rideId;
        this.activity = activity;
        this.color = color;
    }

    @Override
    public RequestersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_requester, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final RequestersAdapter.ViewHolder holder, final int position) {
        final User user = users.get(position);

        holder.name_tv.setText(user.getName());
        holder.course_tv.setText(user.getCourse());

        Picasso.with(activity).load(user.getProfilePicUrl())
                .placeholder(R.drawable.user_pic)
                .error(R.drawable.user_pic)
                .transform(new RoundedTransformation(0))
                .into(holder.photo_iv);
        holder.photo_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ProfileAct.class);
                intent.putExtra("user", new Gson().toJson(user));
                intent.putExtra("from", "requesters");
                activity.startActivity(intent);
            }
        });

        holder.accept_bt.setTextColor(color);
        holder.accept_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getNetworkService().answerJoinRequest(new JoinRequestIDsForJson(user.getDbId(), rideId, true), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Util.toast(R.string.requestAccepted);
                        users.remove(user);
                        notifyItemRemoved(holder.getAdapterPosition());

                        new CheckSubGcmTopic().execute(rideId + "");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("answerJoinRequest", error.getMessage());

                        Util.toast(R.string.errorAnsweRequest);
                    }
                });
            }
        });

        holder.reject_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getNetworkService().answerJoinRequest(new JoinRequestIDsForJson(user.getDbId(), rideId, false), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Util.toast(R.string.requestRejected);
                        users.remove(user);
                        notifyItemRemoved(holder.getAdapterPosition());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("answerJoinRequest", error.getMessage());

                        Util.toast(R.string.errorAnsweRequest);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo_iv;
        public TextView name_tv;
        public TextView course_tv;
        public Button accept_bt;
        public Button reject_bt;

        public ViewHolder(View itemView) {
            super(itemView);

            photo_iv = (ImageView) itemView.findViewById(R.id.photo_iv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            course_tv = (TextView) itemView.findViewById(R.id.course_tv);
            accept_bt = (Button) itemView.findViewById(R.id.accept_bt);
            reject_bt = (Button) itemView.findViewById(R.id.reject_bt);
        }
    }
}
