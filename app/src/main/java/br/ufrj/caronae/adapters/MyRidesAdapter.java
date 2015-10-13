package br.ufrj.caronae.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideIdForJson;
import br.ufrj.caronae.models.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.ViewHolder> {
    private final List<Ride> rides;
    private final MainAct activity;

    public MyRidesAdapter(List<Ride> rides, MainAct activity) {
        this.rides = rides;
        this.activity = activity;
    }

    @Override
    public MyRidesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_myride, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final MyRidesAdapter.ViewHolder holder, final int position) {
        final Ride ride = rides.get(position);

        try {
            holder.time_tv.setText(DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(new SimpleDateFormat("HH:mm").parse(ride.getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.slots_tv.setText(ride.getSlots() + " vagas");
        holder.direction_tv.setText(ride.isGoing() ? "Indo para o fundão" : "Voltando do fundão - HUB:" + ride.getHub());
        holder.neighborhood_tv.setText(ride.getNeighborhood());
        String s;
        if (ride.isRoutine()) {
            s = ride.isMonday() ? "S" : "";
            s += ride.isTuesday() ? "T" : "";
            s += ride.isWednesday() ? "Q" : "";
            s += ride.isThursday() ? "Q" : "";
            s += ride.isFriday() ? "S" : "";
            s += ride.isSaturday() ? "S" : "";
        } else {
            s = "Não é rotina";
        }
        holder.routine_tv.setText(s);
        holder.delete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.delete_bt.setVisibility(View.GONE);
                App.getNetworkService().deleteRide(new RideIdForJson(ride.getDbId()), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(App.inst(), "Carona excluída", Toast.LENGTH_SHORT).show();
                        rides.remove(ride);
                        notifyItemRemoved(position);
                        ride.delete();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(App.inst(), "Erro ao excluir carona", Toast.LENGTH_SHORT).show();
                        Log.e("deleteRide", error.getMessage());
                    }
                });
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd = ProgressDialog.show(activity, "", "Aguarde", true, true);
                App.getNetworkService().getRequesters(ride.getDbId()+"", new Callback<List<User>>() {
                    @Override
                    public void success(List<User> users, Response response) {
                        pd.dismiss();
                        if (users.isEmpty()) {
                            Toast.makeText(App.inst(), "Nenhuma solicitação para esse anúncio", Toast.LENGTH_SHORT).show();
                        } else {
                            activity.showRequestersListFrag(users, ride.getDbId());
                            Toast.makeText(App.inst(), users.size()+"", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pd.dismiss();
                        Toast.makeText(App.inst(), "Erro", Toast.LENGTH_SHORT).show();
                        Log.e("getRequesters", error.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time_tv;
        public TextView direction_tv;
        public TextView neighborhood_tv;
        public TextView routine_tv;
        public TextView slots_tv;
        public Button delete_bt;
        public RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);

            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            direction_tv = (TextView) itemView.findViewById(R.id.direction_tv);
            neighborhood_tv = (TextView) itemView.findViewById(R.id.neighborhood_tv);
            routine_tv = (TextView) itemView.findViewById(R.id.routine_tv);
            slots_tv = (TextView) itemView.findViewById(R.id.slots_tv);
            delete_bt = (Button) itemView.findViewById(R.id.delete_bt);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
        }
    }
}
