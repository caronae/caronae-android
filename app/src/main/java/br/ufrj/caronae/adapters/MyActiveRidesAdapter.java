package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideIdForJson;
import br.ufrj.caronae.models.RideWithUsers;
import br.ufrj.caronae.models.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyActiveRidesAdapter extends RecyclerView.Adapter<MyActiveRidesAdapter.ViewHolder> {

    private final List<RideWithUsers> ridesList;
    private final MainAct activity;

    public MyActiveRidesAdapter(List<RideWithUsers> ridesList, MainAct activity) {
        this.ridesList = ridesList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_myactiveride, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RideWithUsers rideWithUsers = ridesList.get(position);

        final Ride ride = rideWithUsers.getRide();
        User driver = rideWithUsers.getUsers().get(0);

        rideWithUsers.getUsers().remove(0);

        ride.setDbId(ride.getId().intValue());
        holder.neighborhood_tv.setText(ride.getNeighborhood());
        holder.go_tv.setText(ride.isGoing() ? "Indo ao fundão" : "Voltando do fundão");
        holder.name_tv.setText(driver.getName());
        holder.course_tv.setText(driver.getCourse());
        try {
            holder.time_tv.setText(DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(new SimpleDateFormat("HH:mm").parse(ride.getTime())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.date_tv.setText(ride.getDate());
        holder.carModel_tv.setText(driver.getCarModel());
        holder.carColor_tv.setText(driver.getCarColor());
        holder.carPlate_tv.setText(driver.getCarPlate());
        holder.description_tv.setText(ride.getDescription());
        holder.ridersList.setAdapter(new RidersAdapter(rideWithUsers.getUsers(), activity));
        holder.ridersList.setHasFixedSize(true);
        holder.ridersList.setLayoutManager(new LinearLayoutManager(activity));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        holder.layout.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rideWithUsers.getUsers().size() * 30 + 230, activity.getResources().getDisplayMetrics());

        holder.leave_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getNetworkService().leaveRide(new RideIdForJson(ride.getDbId()), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        App.toast("Carona excluída");
                        ridesList.remove(rideWithUsers);
                        notifyItemRemoved(position);

                        List<Ride> rides = Ride.find(Ride.class, "zone = ? and neighborhood = ? and date = ? and time = ?", ride.getZone(), ride.getNeighborhood(), ride.getDate(), ride.getTime());
                        if (rides != null && !rides.isEmpty())
                            rides.get(0).delete();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        App.toast("Erro ao desistir de carona");
                        Log.e("leaveRide", error.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return ridesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView neighborhood_tv;
        public TextView go_tv;
        public TextView name_tv;
        public TextView course_tv;
        public TextView time_tv;
        public TextView date_tv;
        public TextView carModel_tv;
        public TextView carColor_tv;
        public TextView carPlate_tv;
        public TextView description_tv;
        public Button leave_bt;
        public RelativeLayout layout;
        public RecyclerView ridersList;

        public ViewHolder(View itemView) {
            super(itemView);

            neighborhood_tv = (TextView) itemView.findViewById(R.id.neighborhood_tv);
            go_tv = (TextView) itemView.findViewById(R.id.go_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            course_tv = (TextView) itemView.findViewById(R.id.course_tv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            carModel_tv = (TextView) itemView.findViewById(R.id.carModel_tv);
            carColor_tv = (TextView) itemView.findViewById(R.id.carColor_tv);
            carPlate_tv = (TextView) itemView.findViewById(R.id.carPlate_tv);
            description_tv = (TextView) itemView.findViewById(R.id.description_tv);
            leave_bt = (Button) itemView.findViewById(R.id.leave_bt);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            ridersList = (RecyclerView) itemView.findViewById(R.id.ridersList);
        }
    }
}
