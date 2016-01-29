package br.ufrj.caronae.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.acts.RequestersListAct;
import br.ufrj.caronae.models.Ride;
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

        int color = 0;
        if (ride.getZone().equals("Centro")) {
            color = ContextCompat.getColor(activity, R.color.zone_centro);
        }
        if (ride.getZone().equals("Zona Sul")) {
            color = ContextCompat.getColor(activity, R.color.zone_sul);
        }
        if (ride.getZone().equals("Zona Oeste")) {
            color = ContextCompat.getColor(activity, R.color.zone_oeste);
        }
        if (ride.getZone().equals("Zona Norte")) {
            color = ContextCompat.getColor(activity, R.color.zone_norte);
        }
        if (ride.getZone().equals("Baixada")) {
            color = ContextCompat.getColor(activity, R.color.zone_baixada);
        }
        if (ride.getZone().equals("Grande Niterói")) {
            color = ContextCompat.getColor(activity, R.color.zone_niteroi);
        }
        if (ride.getZone().equals("Outros")) {
            color = ContextCompat.getColor(activity, R.color.zone_outros);
        }
        holder.cardView.setCardBackgroundColor(color);

        if (ride.isGoing())
            holder.time_tv.setText(activity.getString(R.string.arrivingAt, ride.getTime()));
        else
            holder.time_tv.setText(activity.getString(R.string.leavingAt, ride.getTime()));
        holder.date_tv.setText(Util.formatGoodDateWithoutYear(ride.getDate()));
        holder.slots_tv.setText(activity.getString(R.string.Xslots, ride.getSlots(), (Integer.parseInt(ride.getSlots()) > 1 ? "s" : "")));
        String location;
        if (ride.isGoing())
            location = ride.getNeighborhood() + " ➜ " + ride.getHub();
        else
            location = ride.getHub() + " ➜ " + ride.getNeighborhood();
        holder.location_tv.setText(location);

        String s = activity.getString(R.string.repeats);
        if (ride.isRoutine()) {
            s += ride.getWeekDays().contains("7") ? activity.getString(R.string.sunday) : "";
            s += ride.getWeekDays().contains("1") ? activity.getString(R.string.monday) : "";
            s += ride.getWeekDays().contains("2") ? activity.getString(R.string.tuesday) : "";
            s += ride.getWeekDays().contains("3") ? activity.getString(R.string.wednesday) : "";
            s += ride.getWeekDays().contains("4") ? activity.getString(R.string.thursday) : "";
            s += ride.getWeekDays().contains("5") ? activity.getString(R.string.friday) : "";
            s += ride.getWeekDays().contains("6") ? activity.getString(R.string.saturday) : "";
            s = s.substring(0, s.length() - 1);
        } else {
            s = activity.getString(R.string.notRoutine);
        }
        holder.routine_tv.setText(s);

        holder.delete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getNetworkService().deleteRide(ride.getDbId() + "", new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Util.toast(R.string.rideDeleted);
                        rides.remove(ride);
                        notifyItemRemoved(holder.getAdapterPosition());
                        ride.delete();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        activity.getString(R.string.errorRideDeleted);
                        Log.e("deleteRide", error.getMessage());
                    }
                });
            }
        });

        final int colorToSend = color;
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd = ProgressDialog.show(activity, "", activity.getResources().getString(R.string.wait), true, true);

                App.getNetworkService().getRequesters(ride.getDbId() + "", new Callback<List<User>>() {
                    @Override
                    public void success(List<User> users, Response response) {
                        pd.dismiss();

                        if (users.isEmpty()) {
                            Util.toast(R.string.noRequesters);
                        } else {
                            Intent intent = new Intent(activity, RequestersListAct.class);
                            intent.putParcelableArrayListExtra("users", (ArrayList<User>) users);
                            intent.putExtra("rideId", ride.getDbId());
                            intent.putExtra("color", colorToSend);
                            activity.startActivity(intent);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pd.dismiss();

                        Util.toast(R.string.errorGetRequesters);

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
        public TextView location_tv;
        public TextView routine_tv;
        public TextView slots_tv;
        public TextView date_tv;
        public Button delete_bt;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            location_tv = (TextView) itemView.findViewById(R.id.location_tv);
            routine_tv = (TextView) itemView.findViewById(R.id.routine_tv);
            slots_tv = (TextView) itemView.findViewById(R.id.slots_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            delete_bt = (Button) itemView.findViewById(R.id.delete_bt);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}
