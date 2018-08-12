package br.ufrj.caronae.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.R;
import br.ufrj.caronae.customizedviews.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.RideDetailAct;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.ViewHolder> {

    private final int TYPE_HEADER = 0;
    private final int TYPE_BODY = 1;

    private final Context context;
    private List<Object> mixedList;

    public MyRidesAdapter(Context context) {
        this.context = context;
        this.mixedList = new ArrayList<>();
    }

    @Override
    public MyRidesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView;

        if (viewType == TYPE_HEADER) {
            contactView = inflater.inflate(R.layout.separator_all_rides, parent, false);
        } else if (viewType == TYPE_BODY) {
            contactView = inflater.inflate(R.layout.item_my_rides_card, parent, false);
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
    public void onBindViewHolder(final MyRidesAdapter.ViewHolder viewHolder, int position) {
        if (!(mixedList == null || mixedList.size() == 0)) {
            if (mixedList.get(position).getClass().equals(RideForJson.class)) {
                final RideForJson rideOffer = (RideForJson) mixedList.get(position);
                if(rideOffer.type != null) {
                    if (rideOffer.type.equals("Ativas") || rideOffer.type.equals("Ofertadas") || rideOffer.type.equals("Pendentes")) {
                        viewHolder.typeCardText.setText(rideOffer.type);
                        viewHolder.typeCard.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.typeCard.setVisibility(View.GONE);
                    }
                }
                else{
                    viewHolder.typeCard.setVisibility(View.GONE);
                }
                if(rideOffer.showWarningText)
                {
                    viewHolder.secondaryLay.setVisibility(View.VISIBLE);
                }
                else{
                    viewHolder.secondaryLay.setVisibility(View.GONE);
                }

                int color = Util.getColors(rideOffer.getZone());
                viewHolder.location_tv.setTextColor(color);
                viewHolder.time_tv.setTextColor(color);
                viewHolder.name_tv.setTextColor(color);
                viewHolder.photo_iv.setBorderColor(color);
                try {
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
                }
                catch (Exception e)
                {
                    Util.debug("Loading failled.");
                }
                String timeText;
                if (rideOffer.isGoing())
                    timeText = context.getResources().getString(R.string.arriving_at, Util.formatTime(rideOffer.getTime()));
                else
                    timeText = context.getResources().getString(R.string.leaving_at, Util.formatTime(rideOffer.getTime()));

                timeText =  timeText + " | " + Util.getWeekDayFromDateWithoutTodayString(rideOffer.getDate()) + " | " +Util.formatBadDateWithoutYear(rideOffer.getDate());
                viewHolder.time_tv.setText(timeText);

                String name = rideOffer.getDriver().getName();
                try {
                    String[] split = name.split(" ");
                    String shortName = split[0] + " " + split[split.length - 1];
                    viewHolder.name_tv.setText(shortName);
                } catch (Exception e) {
                    viewHolder.name_tv.setText(name);
                }

                String location;
                if (rideOffer.isGoing())
                    location = rideOffer.getNeighborhood().toUpperCase() + " ➜ " + rideOffer.getHub().toUpperCase();
                else
                    location = rideOffer.getHub().toUpperCase() + " ➜ " + rideOffer.getNeighborhood().toUpperCase();

                viewHolder.location_tv.setText(location);

                viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, RideDetailAct.class);
                        intent.putExtra("fromWhere", rideOffer.fromWhere);
                        intent.putExtra("ride", rideOffer);
                        intent.putExtra("id", rideOffer.getId().intValue());
                        intent.putExtra("starting", true);
                        intent.putExtra("requested", true);
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    public void makeList(List<RideForJson> rideOffers) {
        mixedList.clear();
        mixedList.addAll(rideOffers);
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
        public TextView time_tv, name_tv, location_tv, typeCardText, notificationCounter;
        private RelativeLayout typeCard, parentLayout, secondaryLay, notificationLay;

        private ViewHolder(View itemView) {
            super(itemView);
            photo_iv = itemView.findViewById(R.id.photo_iv);
            time_tv = itemView.findViewById(R.id.time_tv);
            location_tv = itemView.findViewById(R.id.location_tv);
            name_tv = itemView.findViewById(R.id.name_tv);
            parentLayout = itemView.findViewById(R.id.cardView);
            typeCard = itemView.findViewById(R.id.typeCard);
            typeCardText = itemView.findViewById(R.id.typeCardText);
            secondaryLay = itemView.findViewById(R.id.secondary_lay);
            notificationLay = itemView.findViewById(R.id.notification);
            notificationCounter = itemView.findViewById(R.id.notification_count);
        }
    }
}