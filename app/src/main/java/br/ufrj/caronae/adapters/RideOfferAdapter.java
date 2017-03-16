package br.ufrj.caronae.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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

    private final int TYPE_HEADER = 0;
    private final int TYPE_BODY = 1;
    private final int TYPE_ZERO = 2;

    private final Context context;
    private List<RideForJson> rideOffers;
    private FragmentManager fm;
    private List<Object> mixedList;

    public RideOfferAdapter(List<RideForJson> rideOffers, Context context, FragmentManager fm) {
        this.rideOffers = rideOffers;
        this.context = context;
        this.fm = fm;
        List<Object> mixedList = new ArrayList<Object>();

    }

    @Override
    public RideOfferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = null;

        if (viewType == TYPE_HEADER) {
            contactView = inflater.inflate(R.layout.list_separator, parent, false);
        } else if (viewType == TYPE_BODY) {
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
        if (mixedList.get(position).getClass() == Integer.class) {
            return TYPE_HEADER;
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

                if (rideOffer.getDriver().getDbId() != App.getUser().getDbId())
                    viewHolder.photo_iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ProfileAct.class);
                            intent.putExtra("user", new Gson().toJson(rideOffer.getDriver()));
                            intent.putExtra("from", "rideOffer");
                            context.startActivity(intent);
                        }
                    });

                String timeText;
                if (rideOffer.isGoing())
                    timeText = context.getResources().getString(R.string.arrivingAt, Util.formatTime(rideOffer.getTime()));
                else
                    timeText = context.getResources().getString(R.string.leavingAt, Util.formatTime(rideOffer.getTime()));
                viewHolder.time_tv.setText(timeText);
                viewHolder.date_tv.setText(Util.formatBadDateWithoutYear(rideOffer.getDate()));

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
                context.startActivity(intent);

//                        RideDetailDialogFrag detailFrag = new RideDetailDialogFrag();
//                        Bundle args = new Bundle();
//                        args.putParcelable("ride", rideOffer);
//                        args.putBoolean("requested", finalRequested);
//                        detailFrag.setArguments(args);
//                        detailFrag.show(fm, "a");
                    }
                });
            } else {
                viewHolder.header_text.setText(getDateText((RideForJson) mixedList.get(position + 1)));

//                viewHolder.header_card.setBackground(ContextCompat.getDrawable(context, getRandomColor()));
            }
        }
    }

    public void makeList(List<RideForJson> rideOffers) {
        this.rideOffers = rideOffers;
        List<Integer> headerPositions = getHeaderPositionsOnList(rideOffers);
        mixedList = new ArrayList<>();
        mixedList.addAll(rideOffers);
        if (headerPositions != null && headerPositions.size() > 0) {
            for (int headerCount = 0; headerCount < headerPositions.size(); headerCount++) {
                mixedList.add(headerPositions.get(headerCount) + headerCount, headerPositions.get(headerCount));
            }
        }
        notifyDataSetChanged();
    }

    public void addToList(List<RideForJson> rideOffers) {
        this.rideOffers.addAll(rideOffers);
        notifyDataSetChanged();
    }

    public void remove(int rideId) {
        for (int i = 0; i < rideOffers.size(); i++)
            if (rideOffers.get(i).getDbId() == rideId) {
                rideOffers.remove(i);
                notifyItemRemoved(i);
            }
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
        public TextView time_tv;
        public TextView date_tv;
        public TextView location_tv;
        public TextView name_tv;
        public RelativeLayout parentLayout;

        public TextView header_text;
        public FrameLayout header_card;

        public ViewHolder(View itemView) {
            super(itemView);

            photo_iv = (CircleImageView) itemView.findViewById(R.id.photo_iv);
            requestIndicator_iv = (ImageView) itemView.findViewById(R.id.requestIndicator_iv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            location_tv = (TextView) itemView.findViewById(R.id.location_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.item_rideoffer_parent_layout);

            header_text = (TextView) itemView.findViewById(R.id.list_separator_text);
            header_card = (FrameLayout) itemView.findViewById(R.id.card_list_separator);
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

    private String getDateText(RideForJson ride) {
        String dateString = ride.getDate();
        String weekDay = Util.getWeekDayFromDate(dateString);
        if (weekDay.equals("Hoje") || weekDay.equals("Amanhã")){
            return weekDay;
        }
        return weekDay + " - " + Util.getDayWithMonthFromDate(dateString);
    }
}
