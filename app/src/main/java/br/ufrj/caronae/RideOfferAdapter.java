package br.ufrj.caronae;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.ufrj.caronae.models.RideOffer;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
    public void onBindViewHolder(final RideOfferAdapter.ViewHolder viewHolder, int position) {
        final RideOffer rideOffer = rideOffers.get(position);

        viewHolder.time_tv.setText(rideOffer.getTime());
        viewHolder.name_tv.setText(rideOffer.getDriverName());
        viewHolder.slots_tv.setText(rideOffer.getSlots() + " vagas");
        viewHolder.direction_tv.setText(rideOffer.isGo() ? "Indo para o fundão" : "Voltando do fundão - HUB:" + rideOffer.getHub());
        viewHolder.neighborhood_tv.setText(rideOffer.getNeighborhood());
        viewHolder.join_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.join_bt.setVisibility(View.GONE);
                App.getNetworkService().sendJoinRequest(rideOffer.getRideId(), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(App.inst(), "Solicitação enviada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(App.inst(), "Erro no envio da solicitação", Toast.LENGTH_SHORT).show();
                        Log.e(App.LOGTAG, error.getMessage());
                    }
                });
            }
        });
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
        public Button join_bt;

        public ViewHolder(View itemView) {
            super(itemView);

            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            direction_tv = (TextView) itemView.findViewById(R.id.direction_tv);
            neighborhood_tv = (TextView) itemView.findViewById(R.id.neighborhood_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            slots_tv = (TextView) itemView.findViewById(R.id.slots_tv);
            join_bt = (Button) itemView.findViewById(R.id.join_bt);
        }
    }
}
