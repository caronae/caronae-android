package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.modelsforjson.HistoryRideForJson;

public class RidesHistoryAdapter extends RecyclerView.Adapter<RidesHistoryAdapter.ViewHolder> {

    private final List<HistoryRideForJson> historyRides;
    private final MainAct activity;

    public RidesHistoryAdapter(List<HistoryRideForJson> historyRides, MainAct activity) {
        this.historyRides = historyRides;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_ridehistory, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(RidesHistoryAdapter.ViewHolder holder, int position) {
        final HistoryRideForJson historyRide = historyRides.get(position);
        Ride ride = historyRide.getRide();

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

        holder.time_tv.setText(activity.getString(R.string.arrivedAt, Util.formatTime(ride.getTime()) + " | "));
        holder.time_tv.setTextColor(color);
        holder.date_tv.setText(Util.formatBadDateWithoutYear(ride.getDate()));
        holder.date_tv.setTextColor(color);
        holder.slots_tv.setText(activity.getString(R.string.Xriders, historyRide.getRidersCount(), historyRide.getRidersCount() > 1 ? "s" : ""));
        holder.slots_tv.setTextColor(color);
        String location;
        if (ride.isGoing())
            location = ride.getNeighborhood() + " -> " + ride.getHub();
        else
            location = ride.getHub() + " -> " + ride.getNeighborhood();
        holder.location_tv.setText(location);
        holder.location_tv.setTextColor(color);

        Picasso.with(activity).load(historyRide.getDriverPic())
                .placeholder(R.drawable.user_pic)
                .error(R.drawable.user_pic)
                .transform(new RoundedTransformation(0))
                .into(holder.photo_iv);
    }

    @Override
    public int getItemCount() {
        return historyRides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time_tv;
        public TextView location_tv;
        public TextView slots_tv;
        public TextView date_tv;
        public ImageView photo_iv;

        public ViewHolder(View itemView) {
            super(itemView);

            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            location_tv = (TextView) itemView.findViewById(R.id.location_tv);
            slots_tv = (TextView) itemView.findViewById(R.id.slots_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            photo_iv = (ImageView) itemView.findViewById(R.id.photo_iv);
        }
    }
}
