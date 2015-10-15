package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideWithUsers;
import br.ufrj.caronae.models.User;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        RideWithUsers rideWithUsers = ridesList.get(position);

        Ride ride = rideWithUsers.getRide();
        User driver = rideWithUsers.getUsers().get(0);

        holder.neighborhood_tv.setText(ride.getNeighborhood());
        holder.go_tv.setText(ride.isGoing() ? "Indo ao fundão" : "Voltando do fundão");
        holder.name_tv.setText(driver.getName());
        holder.course_tv.setText(driver.getCourse());
        holder.time_tv.setText(ride.getTime());
        holder.date_tv.setText(ride.getDate());
        holder.description_tv.setText(ride.getDescription());
        holder.ridersList.setAdapter(new RidersAdapter(rideWithUsers.getUsers(), activity));
        holder.ridersList.setHasFixedSize(true);
        holder.ridersList.setLayoutManager(new LinearLayoutManager(activity));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, displaymetrics.heightPixels, activity.getResources().getDisplayMetrics());

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rideWithUsers.getUsers().size() * 25 + 135, activity.getResources().getDisplayMetrics());

        holder.layout.getLayoutParams().height = height;
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
        public TextView description_tv;
        public Button giveup_bt;
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
            description_tv = (TextView) itemView.findViewById(R.id.description_tv);
            giveup_bt = (Button) itemView.findViewById(R.id.giveup_bt);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            ridersList = (RecyclerView) itemView.findViewById(R.id.ridersList);
        }
    }
}
