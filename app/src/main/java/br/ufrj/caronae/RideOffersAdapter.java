package br.ufrj.caronae;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.ufrj.caronae.models.RideOffer;

public class RideOffersAdapter extends
        RecyclerView.Adapter<RideOffersAdapter.ViewHolder> {

    private List<RideOffer> rideOffers;

    public RideOffersAdapter(List<RideOffer> rideOffers) {
        this.rideOffers = rideOffers;
    }

    @Override
    public RideOffersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_rideoffer, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(RideOffersAdapter.ViewHolder viewHolder, int position) {
        RideOffer rideOffer = rideOffers.get(position);

        TextView time = viewHolder.time_tv;
        time.setText(rideOffer.getTime());
        TextView direction = viewHolder.direction_tv;
        direction.setText(rideOffer.isGo() ? "Indo para o fundão" : "Voltando do fundão");
        TextView neighborhood = viewHolder.neighborhood_tv;
        neighborhood.setText(rideOffer.getNeighborhood());
    }

    @Override
    public int getItemCount() {
        return rideOffers.size();
    }

    public void makeList(List<RideOffer> l) {
        rideOffers = l;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time_tv;
        public TextView direction_tv;
        public TextView neighborhood_tv;
        public TextView name_tv;

        public ViewHolder(View itemView) {
            super(itemView);

            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            direction_tv = (TextView) itemView.findViewById(R.id.direction_tv);
            neighborhood_tv = (TextView) itemView.findViewById(R.id.neighborhood_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
        }
    }
}
