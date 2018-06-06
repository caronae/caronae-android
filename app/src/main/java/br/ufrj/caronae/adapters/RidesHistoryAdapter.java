package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.R;
import br.ufrj.caronae.customizedviews.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.models.RideHistory;
import de.hdodenhof.circleimageview.CircleImageView;

public class RidesHistoryAdapter extends RecyclerView.Adapter<RidesHistoryAdapter.ViewHolder> {

    private final int TYPE_HEADER = 0;
    private final int TYPE_BODY = 1;

    private final Context context;

    private List<Object> mixedList;

    public RidesHistoryAdapter(Context ctx) {
        this.context = ctx;
        this.mixedList = new ArrayList<>();
    }

    @Override
    public RidesHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView;
        if (viewType == TYPE_HEADER) {
            contactView = inflater.inflate(R.layout.separator_all_rides, parent, false);
        } else if (viewType == TYPE_BODY) {
            contactView = inflater.inflate(R.layout.item_ridehistory, parent, false);
        } else {
            contactView = inflater.inflate(R.layout.separator_all_rides, parent, false);
        }
        return new ViewHolder(contactView);
    }



    @Override
    public int getItemViewType(int position) {
        final int TYPE_ZERO = 2;

        if (mixedList == null){
            return TYPE_ZERO;
        } else if (mixedList.size() == 0){
            return TYPE_ZERO;
        }
        if (mixedList.get(position).getClass() == Integer.class) {
            return TYPE_HEADER;
        }
        return TYPE_BODY;
    }

    @Override
    public void onBindViewHolder(final RidesHistoryAdapter.ViewHolder viewHolder, int position) {
        if (!(mixedList == null || mixedList.size() == 0)) {
            if (mixedList.get(position).getClass().equals(RideHistory.class)) {
                final RideHistory rideHistory = (RideHistory) mixedList.get(position);
                int color = Util.getColors(rideHistory.getZone());
                viewHolder.location_tv.setTextColor(color);
                viewHolder.time_tv.setTextColor(color);
                viewHolder.name_tv.setTextColor(color);
                viewHolder.photo_iv.setBorderColor(color);
                String profilePicUrl = rideHistory.getDriver().getProfilePicUrl();
                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                    Picasso.with(context).load(profilePicUrl)
                            .placeholder(R.drawable.user_pic)
                            .error(R.drawable.user_pic)
                            .transform(new RoundedTransformation())
                            .into(viewHolder.photo_iv);
                } else {
                    viewHolder.photo_iv.setImageResource(R.drawable.user_pic);
                }

                String timeText;
                if (rideHistory.isGoing())
                    timeText = context.getResources().getString(R.string.arriving_at, Util.formatTime(rideHistory.getTime()));
                else
                    timeText = context.getResources().getString(R.string.leaving_at, Util.formatTime(rideHistory.getTime()));

                timeText =  timeText + " | " + Util.getWeekDayFromDateWithoutTodayString(rideHistory.getDate()) + " | " +Util.formatBadDateWithoutYear(rideHistory.getDate());
                viewHolder.time_tv.setText(timeText);

                String name = rideHistory.getDriver().getName();

                try {
                    String[] split = name.split(" ");
                    String shortName = split[0] + " " + split[split.length - 1];
                    viewHolder.name_tv.setText(shortName);
                } catch (Exception e) {
                    viewHolder.name_tv.setText(name);
                }

                String location;
                if (rideHistory.isGoing())
                    location = rideHistory.getNeighborhood().toUpperCase() + " ➜ " + rideHistory.getHub().toUpperCase();
                else
                    location = rideHistory.getHub().toUpperCase() + " ➜ " + rideHistory.getNeighborhood().toUpperCase();

                viewHolder.location_tv.setText(location);
            }
        }
    }

    public void makeList(List<RideHistory> rideHistory) {
        mixedList.clear();
        mixedList.addAll(rideHistory);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mixedList == null || mixedList.size() == 0) {
            return 1;
        }
        return mixedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView photo_iv;
        public TextView time_tv;
        public TextView location_tv;
        public TextView name_tv;

        private ViewHolder(View itemView) {
            super(itemView);
            photo_iv = itemView.findViewById(R.id.photo_iv);
            time_tv = itemView.findViewById(R.id.time_tv);
            location_tv = itemView.findViewById(R.id.location_tv);
            name_tv = itemView.findViewById(R.id.name_tv);
        }
    }
}