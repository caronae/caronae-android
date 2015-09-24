package br.ufrj.caronae;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.ufrj.caronae.models.RideOffer;

public class RideOfferAdapter extends
        RecyclerView.Adapter<RideOfferAdapter.ViewHolder> {

    private List<RideOffer> rideOffers;

    public RideOfferAdapter(List<RideOffer> rideOffers) {
        this.rideOffers = rideOffers;
    }

    @Override
    public RideOfferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_rideoffer, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(RideOfferAdapter.ViewHolder viewHolder, int position) {
        RideOffer rideOffer = rideOffers.get(position);

        viewHolder.time_tv.setText(rideOffer.getTime());
        viewHolder.name_tv.setText(rideOffer.getDriverName());
        viewHolder.slots_tv.setText(rideOffer.getSlots() + " vagas");
        viewHolder.direction_tv.setText(rideOffer.isGo() ? "Indo para o fundão" : "Voltando do fundão - HUB:" + rideOffer.getHub());
        viewHolder.neighborhood_tv.setText(rideOffer.getNeighborhood());
    }

    @Override
    public int getItemCount() {
        return rideOffers.size();
    }

    public void makeList(List<RideOffer> rideOffers) {
        this.rideOffers = rideOffers;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time_tv;
        public TextView direction_tv;
        public TextView neighborhood_tv;
        public TextView name_tv;
        public TextView slots_tv;

        public ViewHolder(View itemView) {
            super(itemView);

            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            direction_tv = (TextView) itemView.findViewById(R.id.direction_tv);
            neighborhood_tv = (TextView) itemView.findViewById(R.id.neighborhood_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            slots_tv = (TextView) itemView.findViewById(R.id.slots_tv);
        }
    }
}
