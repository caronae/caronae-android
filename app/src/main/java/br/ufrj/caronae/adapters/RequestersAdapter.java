package br.ufrj.caronae.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.models.JoinRequestIDsForJson;
import br.ufrj.caronae.models.User;
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
    public void onBindViewHolder(RequestersAdapter.ViewHolder holder, final int position) {
        final User user = users.get(position);

        holder.course_tv.setText(user.getCourse());

        holder.name_tv.setText(user.getName());
        holder.name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(activity).
                        setTitle(user.getName()).
                        setMessage(user.getProfile() + "\n" + user.getCourse() + "\nUsuário desde " + user.getCreatedAt().split(" ")[0]).
                        show();
            }
        });

        holder.accept_bt.setTextColor(color);
        holder.accept_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getNetworkService().answerJoinRequest(new JoinRequestIDsForJson(user.getDbId(), rideId, true), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        App.toast("Solicitação aceita");
                        users.remove(user);
                        notifyItemRemoved(position);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("answerJoinRequest", error.getMessage());
                        App.toast("Erro ao responder solicitação");
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
                        App.toast("Solicitação rejeitada");
                        users.remove(user);
                        notifyItemRemoved(position);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("answerJoinRequest", error.getMessage());
                        App.toast("Erro ao responder solicitação");
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
        public TextView name_tv;
        public TextView course_tv;
        public Button accept_bt;
        public Button reject_bt;

        public ViewHolder(View itemView) {
            super(itemView);

            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            course_tv = (TextView) itemView.findViewById(R.id.course_tv);
            accept_bt = (Button) itemView.findViewById(R.id.accept_bt);
            reject_bt = (Button) itemView.findViewById(R.id.reject_bt);
        }
    }
}
