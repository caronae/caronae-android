package br.ufrj.caronae.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.asyncs.UnsubGcmTopic;
import br.ufrj.caronae.acts.ChatAct;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.models.ActiveRideId;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideIdForJson;
import br.ufrj.caronae.models.modelsforjson.RideWithUsersForJson;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyActiveRidesAdapter extends RecyclerView.Adapter<MyActiveRidesAdapter.ViewHolder> {

    private final List<RideWithUsersForJson> ridesList;
    private final MainAct activity;

    public MyActiveRidesAdapter(List<RideWithUsersForJson> ridesList, MainAct activity) {
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final RideWithUsersForJson rideWithUsers = ridesList.get(position);

        final Ride ride = rideWithUsers.getRide();
        final User driver = rideWithUsers.getUsers().get(0);

        rideWithUsers.getUsers().remove(0);

        int color = 0, bgRes = 0;
        if (ride.getZone().equals("Centro")) {
            color = ContextCompat.getColor(activity, R.color.zone_centro);
            bgRes = R.drawable.bg_bt_raise_zone_centro;
            holder.chat_bt.setBackgroundResource(bgRes);
        }
        if (ride.getZone().equals("Zona Sul")) {
            color = ContextCompat.getColor(activity, R.color.zone_sul);
            bgRes = R.drawable.bg_bt_raise_zone_sul;
            holder.chat_bt.setBackgroundResource(R.drawable.bg_bt_raise_zone_sul);
        }
        if (ride.getZone().equals("Zona Oeste")) {
            color = ContextCompat.getColor(activity, R.color.zone_oeste);
            bgRes = R.drawable.bg_bt_raise_zone_oeste;
            holder.chat_bt.setBackgroundResource(R.drawable.bg_bt_raise_zone_oeste);
        }
        if (ride.getZone().equals("Zona Norte")) {
            color = ContextCompat.getColor(activity, R.color.zone_norte);
            bgRes = R.drawable.bg_bt_raise_zone_norte;
            holder.chat_bt.setBackgroundResource(R.drawable.bg_bt_raise_zone_norte);
        }
        if (ride.getZone().equals("Baixada")) {
            color = ContextCompat.getColor(activity, R.color.zone_baixada);
            bgRes = R.drawable.bg_bt_raise_zone_baixada;
            holder.chat_bt.setBackgroundResource(R.drawable.bg_bt_raise_zone_baixada);
        }
        if (ride.getZone().equals("Grande Niterói")) {
            color = ContextCompat.getColor(activity, R.color.zone_niteroi);
            bgRes = R.drawable.bg_bt_raise_zone_niteroi;
            holder.chat_bt.setBackgroundResource(R.drawable.bg_bt_raise_zone_niteroi);
        }
        holder.lay1.setBackgroundColor(color);

        ride.setDbId(ride.getId().intValue());
        final String location;
        if (ride.isGoing())
            location = ride.getNeighborhood() + " -> " + ride.getHub();
        else
            location = ride.getHub() + " -> " + ride.getNeighborhood();
        holder.location_tv.setText(location);
        holder.name_tv.setText(driver.getName());
        holder.way_tv.setText(ride.getRoute());
        holder.place_tv.setText(ride.getPlace());
        holder.phoneNumber_tv.setText(driver.getPhoneNumber());
        holder.course_tv.setText(driver.getCourse());
        holder.time_tv.setText("Chegando ás " + Util.formatTime(ride.getTime()));
        holder.time_tv.setTextColor(color);
        holder.date_tv.setText(Util.formatBadDateWithoutYear(ride.getDate()));
        holder.date_tv.setTextColor(color);
        holder.carModel_tv.setText(driver.getCarModel());
        holder.carColor_tv.setText(driver.getCarColor());
        holder.carPlate_tv.setText(driver.getCarPlate());
        holder.description_tv.setText(ride.getDescription());
        holder.ridersList.setAdapter(new RidersAdapter(rideWithUsers.getUsers(), activity));
        holder.ridersList.setHasFixedSize(true);
        holder.ridersList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        /*DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        holder.layout.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rideWithUsers.getUsers().size() * 30 + 600, activity.getResources().getDisplayMetrics());*/

        final int finalColor = color, finalBgRes = bgRes;
        holder.chat_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ChatAct.class);
                intent.putExtra("rideId", ride.getDbId()+"");
                intent.putExtra("location", location);
                intent.putExtra("color", finalColor);
                intent.putExtra("bgRes", finalBgRes);
                intent.putExtra("date", Util.formatBadDateWithoutYear(ride.getDate()));
                intent.putExtra("time", Util.formatTime(ride.getTime()));

                String riders = driver.getName().split(" ")[0] + ", ";
                for (User user : rideWithUsers.getUsers()) {
                    riders += user.getName().split(" ")[0] + ", ";
                }
                riders = riders.substring(0, riders.length() - 2);
                intent.putExtra("riders", riders);
                activity.startActivity(intent);
            }
        });

        final String rideId = ride.getDbId()+"";
        holder.leave_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getNetworkService().leaveRide(new RideIdForJson(ride.getDbId()), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Util.toast("Carona excluída");
                        ridesList.remove(rideWithUsers);
                        notifyItemRemoved(holder.getAdapterPosition());

                        new UnsubGcmTopic(activity, rideId).execute();

                        List<Ride> rides = Ride.find(Ride.class, "db_id = ?", rideId);
                        if (rides != null && !rides.isEmpty())
                            rides.get(0).delete();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Util.toast("Erro ao desistir de carona");
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
        public TextView location_tv;
        public TextView name_tv;
        public TextView course_tv;
        public TextView time_tv;
        public TextView date_tv;
        public TextView way_tv;
        public TextView place_tv;
        public TextView carModel_tv;
        public TextView carColor_tv;
        public TextView carPlate_tv;
        public TextView description_tv;
        public TextView phoneNumber_tv;
        public Button leave_bt;
        public Button chat_bt;
        public RelativeLayout lay1;
        public RelativeLayout layout;
        public RecyclerView ridersList;

        public ViewHolder(View itemView) {
            super(itemView);

            location_tv = (TextView) itemView.findViewById(R.id.location_tv);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            course_tv = (TextView) itemView.findViewById(R.id.course_tv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            way_tv = (TextView) itemView.findViewById(R.id.way_tv);
            place_tv = (TextView) itemView.findViewById(R.id.place_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            carModel_tv = (TextView) itemView.findViewById(R.id.carModel_tv);
            carColor_tv = (TextView) itemView.findViewById(R.id.carColor_tv);
            carPlate_tv = (TextView) itemView.findViewById(R.id.carPlate_tv);
            description_tv = (TextView) itemView.findViewById(R.id.description_tv);
            phoneNumber_tv = (TextView) itemView.findViewById(R.id.phoneNumber_tv);
            leave_bt = (Button) itemView.findViewById(R.id.leave_bt);
            chat_bt = (Button) itemView.findViewById(R.id.chat_bt);
            lay1 = (RelativeLayout) itemView.findViewById(R.id.lay1);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            ridersList = (RecyclerView) itemView.findViewById(R.id.ridersList);
        }
    }
}
