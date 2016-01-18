package br.ufrj.caronae.adapters;

import android.content.Context;
import android.content.Intent;
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

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.ProfileAct;
import br.ufrj.caronae.models.RideRequest;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideIdForJson;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RideOfferAdapter extends RecyclerView.Adapter<RideOfferAdapter.ViewHolder> {

    private final FragmentActivity activity;
    private List<RideForJson> rideOffers;
    private List<RideRequest> rideRequests;

    public RideOfferAdapter(List<RideForJson> rideOffers, FragmentActivity activity) {
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
        final RideForJson rideOffer = rideOffers.get(position);

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
        if (rideOffer.getZone().equals("Outros")) {
            color = ContextCompat.getColor(activity, R.color.zone_outros);
        }
        viewHolder.cardView.setCardBackgroundColor(color);

        String profilePicUrl = rideOffer.getDriver().getProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Picasso.with(activity).load(profilePicUrl)
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation(0))
                    .into(viewHolder.photo_iv);
        }


        viewHolder.photo_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ProfileAct.class);
                intent.putExtra("user", new Gson().toJson(rideOffer.getDriver()));
                intent.putExtra("from", "rideOffer");
                activity.startActivity(intent);
            }
        });

        viewHolder.time_tv.setText(Util.formatTime(rideOffer.getTime()));
        viewHolder.date_tv.setText(Util.formatBadDateWithoutYear(rideOffer.getDate()));
        viewHolder.course_tv.setText(rideOffer.getDriver().getCourse());
        viewHolder.name_tv.setText(rideOffer.getDriver().getName());
        String slots = activity.getString(R.string.Xslots, rideOffer.getSlots(), (Integer.parseInt(rideOffer.getSlots()) > 1 ? "s" : ""));
        viewHolder.slots_tv.setText(slots);
        String location;
        if (rideOffer.isGoing())
            location = rideOffer.getNeighborhood() + " ➜ " + rideOffer.getHub();
        else
            location = rideOffer.getHub() + " ➜ " + rideOffer.getNeighborhood();
        viewHolder.location_tv.setText(location);

        int visibility = View.VISIBLE;
        if (rideOffer.getDriver().getDbId() == App.getUser().getDbId()) {
            visibility = View.INVISIBLE;
        } else {
            if (rideRequests != null && !rideRequests.isEmpty()) {
                for (RideRequest rideRequest : rideRequests) {
                    if (rideRequest.getDbId() == rideOffer.getDbId() && rideRequest.isGoing() == rideOffer.isGoing()) {
                        visibility = View.INVISIBLE;
                        break;
                    }
                }
            }
        }
        viewHolder.join_bt.setVisibility(visibility);

        viewHolder.join_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getNetworkService().requestJoin(new RideIdForJson(rideOffer.getDbId()), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Util.toast(R.string.requestSent);

                        new RideRequest(rideOffer.getDbId(), rideOffer.isGoing(), rideOffer.getDate()).save();

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

    public void makeList(List<RideForJson> rideOffers) {
        this.rideOffers = rideOffers;
        if (rideOffers != null && !rideOffers.isEmpty())
            rideRequests = RideRequest.find(RideRequest.class, "date = ?", rideOffers.get(0).getDate());
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
