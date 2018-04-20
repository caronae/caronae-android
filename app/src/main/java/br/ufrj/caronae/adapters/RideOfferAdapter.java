package br.ufrj.caronae.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.ProfileAct;
import br.ufrj.caronae.acts.RideOfferAct;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import de.hdodenhof.circleimageview.CircleImageView;

public class RideOfferAdapter extends RecyclerView.Adapter<RideOfferAdapter.ViewHolder> {

    private final int TYPE_BODY = 1;
    private static final int TYPE_ZERO = 2;

    private final Context context;
    private List<RideForJson> rideOffers;
    private FragmentManager fm;
    private List<Object> mixedList;

    public RideOfferAdapter(List<RideForJson> rideOffers, Context context, FragmentManager fm) {
        this.rideOffers = rideOffers;
        this.context = context;
        this.fm = fm;
        List<Object> mixedList = new ArrayList<>();

    }

    @Override
    public RideOfferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = null;

       if (viewType == TYPE_BODY) {
            contactView = inflater.inflate(R.layout.item_rideoffer_flat, parent, false);
        } else {
            contactView = inflater.inflate(R.layout.list_no_rides, parent, false);
        }

        return new ViewHolder(contactView);
    }

    @Override
    public int getItemViewType(int position) {
        if (mixedList == null){
            return TYPE_ZERO;
        } else if (mixedList.size() == 0){
            return TYPE_ZERO;
        }
        return TYPE_BODY;
    }


    @Override
    public void onBindViewHolder(final RideOfferAdapter.ViewHolder viewHolder, int position) {
        if (!(mixedList == null || mixedList.size() == 0)) {
            if (mixedList.get(position).getClass().equals(RideForJson.class)) {
                final RideForJson rideOffer = (RideForJson) mixedList.get(position);

                int color = Util.getColorbyZone(rideOffer.getZone());

                viewHolder.location_tv.setTextColor(color);
                viewHolder.time_tv.setTextColor(color);
                viewHolder.name_tv.setTextColor(color);

                viewHolder.photo_iv.setBorderColor(color);

                String profilePicUrl = rideOffer.getDriver().getProfilePicUrl();
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
                if (rideOffer.isGoing())
                    timeText = context.getResources().getString(R.string.arrivingAt, Util.formatTime(rideOffer.getTime()));
                else
                    timeText = context.getResources().getString(R.string.leavingAt, Util.formatTime(rideOffer.getTime()));

                timeText =  timeText + " | " + getWeekDayFromDate(rideOffer.getDate()) + " | " +Util.formatBadDateWithoutYear(rideOffer.getDate());

                viewHolder.time_tv.setText(timeText);

                String name = rideOffer.getDriver().getName();
                if(name.isEmpty())
                {
                    viewHolder.allHolder.setVisibility(View.GONE);
                }
                try {
                    String[] split = name.split(" ");
                    String shortName = split[0] + " " + split[split.length - 1];
                    viewHolder.name_tv.setText(shortName);
                } catch (Exception e) {
                    viewHolder.name_tv.setText(name);
                }

                String location;
                if (rideOffer.isGoing())
                    location = rideOffer.getNeighborhood() + " ➜ " + rideOffer.getHub();
                else
                    location = rideOffer.getHub() + " ➜ " + rideOffer.getNeighborhood();
                viewHolder.location_tv.setText(location);

                List<RideRequestSent> rideRequests = RideRequestSent.find(RideRequestSent.class, "db_id = ?", rideOffer.getDbId() + "");
                boolean requested = false;
                if (rideRequests != null && !rideRequests.isEmpty())
                    requested = true;

                viewHolder.requestIndicator_iv.setVisibility(requested ? View.VISIBLE : View.INVISIBLE);

                final boolean finalRequested = requested;
                viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, RideOfferAct.class);
                        intent.putExtra("ride", rideOffer);
                        intent.putExtra("requested", finalRequested);
                        intent.putExtra("fromAllRides", true);
                        context.startActivity(intent);

                    }
                });
            }
            else
            {
                remove(position);
            }
        }
    }

    public void makeList(List<RideForJson> rideOffers) {
        this.rideOffers = rideOffers;
        List<Integer> headerPositions = getHeaderPositionsOnList(rideOffers);
        mixedList = new ArrayList<>();
        mixedList.addAll(rideOffers);
        if (headerPositions != null && headerPositions.size() > 0) {
            for (int headerCount = 1; headerCount < headerPositions.size(); headerCount++) {
                mixedList.add(headerPositions.get(headerCount) + headerCount, headerPositions.get(headerCount));
            }
        }
        notifyDataSetChanged();
    }

    public void remove(int rideId) {
        for (int i = 0; i < rideOffers.size(); i++)
            if (rideOffers.get(i).getDbId() == rideId) {
                rideOffers.remove(i);
                notifyItemRemoved(i);
            }
    }

    public static String getWeekDayFromDate(String dateString) {
        int dayOfWeekInt = -1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, 1);
            Date date = format.parse(dateString);
            c.setTime(date);
            dayOfWeekInt = c.get(Calendar.DAY_OF_WEEK);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String dayOfWeek = "";

        switch (dayOfWeekInt) {
            case 1:
                dayOfWeek = "Dom";
                break;
            case 2:
                dayOfWeek = "Seg";
                break;
            case 3:
                dayOfWeek = "Ter";
                break;
            case 4:
                dayOfWeek = "Qua";
                break;
            case 5:
                dayOfWeek = "Qui";
                break;
            case 6:
                dayOfWeek = "Sex";
                break;
            case 7:
                dayOfWeek = "Sáb";
                break;
        }
        return dayOfWeek;
    }
    @Override
    public int getItemCount() {
//        return rideOffers.size()
        if (mixedList == null || mixedList.size() == 0) {
            return 1;
        }
        return mixedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView photo_iv;
        public ImageView requestIndicator_iv;
        public RelativeLayout parentLayout;
        public LinearLayout allHolder;
        public TextView time_tv;
        public TextView location_tv;
        public TextView name_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            allHolder = (LinearLayout) itemView.findViewById(R.id.cardView);
            photo_iv = (CircleImageView) itemView.findViewById(R.id.photo_iv);
            requestIndicator_iv = (ImageView) itemView.findViewById(R.id.requestIndicator_iv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            location_tv = (TextView) itemView.findViewById(R.id.location_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            parentLayout = (RelativeLayout)itemView.findViewById(R.id.mainlayout);
        }
    }

    private List<Integer> getHeaderPositionsOnList(List<RideForJson> rides) {
        List<Integer> headersPositions = new ArrayList<>();
        if (rides != null) {
            if (rides.size() > 0) {
                headersPositions.add(0);
                for (int rideIndex = 1; rideIndex < rides.size(); rideIndex++) {
                    if (Util.getDayFromDate(rides.get(rideIndex).getDate()) > Util.getDayFromDate(rides.get(rideIndex - 1).getDate())) {
                        headersPositions.add(rideIndex);
                    }
                }
                return headersPositions;
            }
        }
        return null;
    }
}
