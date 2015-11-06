package br.ufrj.caronae.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideIdForJson;
import br.ufrj.caronae.models.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.ViewHolder> {
    private final List<Ride> rides;
    private final MainAct activity;

    public MyRidesAdapter(List<Ride> rides, MainAct activity) {
        this.rides = rides;
        this.activity = activity;
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

        int color = 0;
        if (ride.getZone().equals("Centro")) {
            color = ContextCompat.getColor(activity, R.color.zone_centro);
        }
        if (ride.getZone().equals("Zona Sul")) {
            color = ContextCompat.getColor(activity, R.color.zone_sul);
        }
        if (ride.getZone().equals("Zona Oeste")) {
            color = ContextCompat.getColor(activity, R.color.zone_oeste);
        }
        if (ride.getZone().equals("Zona Norte")) {
            color = ContextCompat.getColor(activity, R.color.zone_norte);
        }
        if (ride.getZone().equals("Baixada")) {
            color = ContextCompat.getColor(activity, R.color.zone_baixada);
        }
        if (ride.getZone().equals("Grande Niterói")) {
            color = ContextCompat.getColor(activity, R.color.zone_niteroi);
        }
        holder.cardView.setCardBackgroundColor(color);

        holder.time_tv.setText("Chegando ás " + ride.getTime() + " | ");
        holder.date_tv.setText(App.formatGoodDateWithoutYear(ride.getDate()));
        holder.slots_tv.setText(ride.getSlots() + " vagas | ");
        //holder.direction_tv.setText(ride.isGoing() ? "Indo para o fundão" : "Voltando do fundão - HUB:" + ride.getHub());
        holder.neighborhood_tv.setText(ride.getNeighborhood());

        String s;
        if (ride.isRoutine()) {
            s = ride.getWeekDays().contains("1") ? "S" : "";
            s += ride.getWeekDays().contains("2") ? "T" : "";
            s += ride.getWeekDays().contains("3") ? "Q" : "";
            s += ride.getWeekDays().contains("4") ? "Q" : "";
            s += ride.getWeekDays().contains("5") ? "S" : "";
            s += ride.getWeekDays().contains("6") ? "S" : "";
        } else {
            s = "Não é rotina";
        }
        holder.routine_tv.setText(s);

        holder.delete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.delete_bt.setVisibility(View.GONE);
                App.getNetworkService().deleteRide(new RideIdForJson(ride.getDbId()), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        App.toast("Carona excluída");
                        rides.remove(ride);
                        notifyItemRemoved(position);
                        ride.delete();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        App.toast("Erro ao excluir carona");
                        Log.e("deleteRide", error.getMessage());
                    }
                });
            }
        });

        final int colorToSend = color;
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd = ProgressDialog.show(activity, "", "Aguarde", true, true);
                App.getNetworkService().getRequesters(new RideIdForJson(ride.getDbId()), new Callback<List<User>>() {
                    @Override
                    public void success(List<User> users, Response response) {
                        pd.dismiss();
                        if (users.isEmpty()) {
                            App.toast("Nenhuma solicitação para esse anúncio");
                        } else {
                            activity.showRequestersListFrag(users, ride.getDbId(), colorToSend);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pd.dismiss();
                        App.toast("Erro ao obter solicitações");
                        Log.e("getRequesters", error.getMessage());
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
        //public TextView direction_tv;
        public TextView neighborhood_tv;
        public TextView routine_tv;
        public TextView slots_tv;
        public TextView date_tv;
        public Button delete_bt;
        //public RelativeLayout layout;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            //direction_tv = (TextView) itemView.findViewById(R.id.direction_tv);
            neighborhood_tv = (TextView) itemView.findViewById(R.id.neighborhood_tv);
            routine_tv = (TextView) itemView.findViewById(R.id.routine_tv);
            slots_tv = (TextView) itemView.findViewById(R.id.slots_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            delete_bt = (Button) itemView.findViewById(R.id.delete_bt);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}
