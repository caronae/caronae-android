package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.models.modelsforjson.RideIdForJson;
import br.ufrj.caronae.models.modelsforjson.RideOfferForJson;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RideOfferAdapter extends RecyclerView.Adapter<RideOfferAdapter.ViewHolder> {

    private final FragmentActivity activity;
    private List<RideOfferForJson> rideOffers;

    public RideOfferAdapter(List<RideOfferForJson> rideOffers, FragmentActivity activity) {
        this.rideOffers = rideOffers;
        this.activity = activity;
    }

    @Override
    public RideOfferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_rideoffer, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final RideOfferAdapter.ViewHolder viewHolder, int position) {
        final RideOfferForJson rideOffer = rideOffers.get(position);

        int color = 0;
        if (rideOffer.getZone().equals("Centro")) {
            color = ContextCompat.getColor(activity, R.color.zone_centro);
        }
        if (rideOffer.getZone().equals("Zona Sul")) {
            color = ContextCompat.getColor(activity, R.color.zone_sul);
        }
        if (rideOffer.getZone().equals("Zona Oeste")) {
            color = ContextCompat.getColor(activity, R.color.zone_oeste);
        }
        if (rideOffer.getZone().equals("Zona Norte")) {
            color = ContextCompat.getColor(activity, R.color.zone_norte);
        }
        if (rideOffer.getZone().equals("Baixada")) {
            color = ContextCompat.getColor(activity, R.color.zone_baixada);
        }
        if (rideOffer.getZone().equals("Grande Niterói")) {
            color = ContextCompat.getColor(activity, R.color.zone_niteroi);
        }
        viewHolder.cardView.setCardBackgroundColor(color);

        String profilePicUrl = rideOffer.getProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Picasso.with(activity).load(profilePicUrl)
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation(0))
                    .into(viewHolder.photo_iv);
        }

        viewHolder.time_tv.setText(Util.formatTime(rideOffer.getTime()));
        viewHolder.date_tv.setText(Util.formatBadDateWithoutYear(rideOffer.getDate()));
        viewHolder.course_tv.setText(rideOffer.getCourse());
        viewHolder.name_tv.setText(rideOffer.getDriverName());
        String slots = activity.getString(R.string.Xslots, rideOffer.getSlots(), (Integer.parseInt(rideOffer.getSlots()) > 1 ? "s" : ""));
        viewHolder.slots_tv.setText(slots);
        String location;
        if (rideOffer.isGoing())
            location = rideOffer.getNeighborhood() + " -> " + rideOffer.getHub();
        else
            location = rideOffer.getHub() + " -> " + rideOffer.getNeighborhood();
        viewHolder.location_tv.setText(location);

        viewHolder.join_bt.setVisibility(rideOffer.getDriverId() == App.getUser().getDbId() ? View.GONE : View.VISIBLE);
        viewHolder.join_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getNetworkService().requestJoin(new RideIdForJson(rideOffer.getRideId()), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Util.toast(R.string.requestSent);

                        rideOffers.remove(rideOffer);
                        notifyItemRemoved(viewHolder.getAdapterPosition());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Util.toast(R.string.errorRequestSent);

                        Log.e("requestJoin", error.getMessage());
                    }
                });
            }
        });
    }

    public void makeList(List<RideOfferForJson> rideOffers) {
        this.rideOffers = rideOffers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rideOffers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo_iv;
        public TextView time_tv;
        public TextView date_tv;
        public TextView course_tv;
        public TextView location_tv;
        public TextView name_tv;
        public TextView slots_tv;
        public Button join_bt;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            photo_iv = (ImageView) itemView.findViewById(R.id.photo_iv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            course_tv = (TextView) itemView.findViewById(R.id.course_tv);
            location_tv = (TextView) itemView.findViewById(R.id.location_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            slots_tv = (TextView) itemView.findViewById(R.id.slots_tv);
            join_bt = (Button) itemView.findViewById(R.id.join_bt);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}
