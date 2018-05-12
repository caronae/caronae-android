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
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;

public class RidesHistoryAdapter extends RecyclerView.Adapter<RidesHistoryAdapter.ViewHolder> {

    private final List<RideHistoryForJson> historyRides;
    private final MainAct activity;

    public RidesHistoryAdapter(List<RideHistoryForJson> historyRides, MainAct activity) {
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
    public void onBindViewHolder(final RidesHistoryAdapter.ViewHolder holder, int position) {
        final RideHistoryForJson historyRide = historyRides.get(position);

        int color = Util.getColors(historyRide.getZone());

        if (historyRide.isGoing())
            holder.time_tv.setText(activity.getString(R.string.arrivedAt, historyRide.getTime() + " | "));
        else
            holder.time_tv.setText(activity.getString(R.string.leftAt, historyRide.getTime() + " | "));
        holder.time_tv.setTextColor(color);
        holder.date_tv.setText(Util.formatDateRemoveYear(historyRide.getDate()));
        holder.date_tv.setTextColor(color);
        holder.name_tv.setTextColor(color);
        String location;
        if (historyRide.isGoing()) {
            location = historyRide.getNeighborhood() + " ➜ " + historyRide.getHub();
        } else {
            location = historyRide.getHub() + " ➜ " + historyRide.getNeighborhood();
        }
        holder.location_tv.setText(location);
        holder.location_tv.setTextColor(color);

        User driver = historyRide.getDriver();
        if (driver != null) {
            holder.name_tv.setText(driver.getName());
            String driverPic = driver.getProfilePicUrl();
            if (driverPic != null && !driverPic.isEmpty()) {
                Picasso.with(activity).load(driverPic)
                        .placeholder(R.drawable.user_pic)
                        .error(R.drawable.user_pic)
                        .transform(new RoundedTransformation())
                        .into(holder.photo_iv);
            } else {
                holder.photo_iv.setImageResource(R.drawable.user_pic);
            }
        }
    }

    @Override
    public int getItemCount() {
        return historyRides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time_tv;
        public TextView location_tv;
        public TextView name_tv;
        public TextView date_tv;
        public ImageView photo_iv;

        public ViewHolder(View itemView) {
            super(itemView);

            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            location_tv = (TextView) itemView.findViewById(R.id.location_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            photo_iv = (ImageView) itemView.findViewById(R.id.photo_iv);
        }
    }
}
