package br.ufrj.caronae.adapters;

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

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.models.Ride;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.ViewHolder> {
    private final List<Ride> rides;

    public MyRidesAdapter(List<Ride> rides) {
        this.rides = rides;
    }

    @Override
    public MyRidesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_myride, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final MyRidesAdapter.ViewHolder holder, final int position) {
        final Ride ride = rides.get(position);

        holder.time_tv.setText(ride.getTime());
        holder.slots_tv.setText(ride.getSlots() + " vagas");
        holder.direction_tv.setText(ride.isGoing() ? "Indo para o fundão" : "Voltando do fundão - HUB:" + ride.getHub());
        holder.neighborhood_tv.setText(ride.getNeighborhood());
        String s;
        if (ride.isRoutine()) {
            s = ride.isMonday() ? "S" : "";
            s += ride.isTuesday() ? "T" : "";
            s += ride.isWednesday() ? "Q" : "";
            s += ride.isThursday() ? "Q" : "";
            s += ride.isFriday() ? "S" : "";
            s += ride.isSaturday() ? "S" : "";
        } else {
            s = "Não é rotina";
        }
        holder.routine_tv.setText(s);
        holder.delete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.delete_bt.setVisibility(View.GONE);
                App.getNetworkService().deleteRide(ride.getId().toString(), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(App.inst(), "Carona excluída", Toast.LENGTH_SHORT).show();
                        rides.remove(ride);
                        notifyItemRemoved(position);
                        ride.delete();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(App.inst(), "Erro ao excluir carona", Toast.LENGTH_SHORT).show();
                        Log.e("deleteRide", error.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time_tv;
        public TextView direction_tv;
        public TextView neighborhood_tv;
        public TextView routine_tv;
        public TextView slots_tv;
        public Button delete_bt;

        public ViewHolder(View itemView) {
            super(itemView);

            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            direction_tv = (TextView) itemView.findViewById(R.id.direction_tv);
            neighborhood_tv = (TextView) itemView.findViewById(R.id.neighborhood_tv);
            routine_tv = (TextView) itemView.findViewById(R.id.routine_tv);
            slots_tv = (TextView) itemView.findViewById(R.id.slots_tv);
            delete_bt = (Button) itemView.findViewById(R.id.delete_bt);
        }
    }
}
