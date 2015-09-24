package br.ufrj.caronae;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.ufrj.caronae.models.Ride;

public class RidesAdapter extends
        RecyclerView.Adapter<RidesAdapter.ViewHolder> {

    private List<Ride> rides;

    public RidesAdapter(List<Ride> rides) {
        this.rides = rides;
    }

    @Override
    public RidesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_ride, parent, false);

        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RidesAdapter.ViewHolder viewHolder, int position) {
        Ride ride = rides.get(position);

        TextView time = viewHolder.time;
        time.setText(ride.getTime());
        TextView direction = viewHolder.direction;
        direction.setText(ride.isGo() ? "Indo para o fundão" : "Voltando do fundão");
        TextView neighborhood = viewHolder.neighborhood;
        neighborhood.setText(ride.getNeighborhood());
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView direction;
        public TextView neighborhood;

        public ViewHolder(View itemView) {
            super(itemView);

            time = (TextView) itemView.findViewById(R.id.time);
            direction = (TextView) itemView.findViewById(R.id.direction);
            neighborhood = (TextView) itemView.findViewById(R.id.neighborhood);
        }
    }
}
