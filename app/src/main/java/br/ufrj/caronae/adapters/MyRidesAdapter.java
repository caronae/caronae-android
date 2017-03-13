package br.ufrj.caronae.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.ImageButton;
import com.rey.material.widget.LinearLayout;
import com.rey.material.widget.RelativeLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.ActiveRideAct;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.acts.ProfileAct;
import br.ufrj.caronae.acts.RequestersListAct;
import br.ufrj.caronae.models.NewChatMsgIndicator;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideRequestReceived;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.ViewHolder> {

    private int TYPE_MY_RIDE =                  0;
    private int TYPE_ACTIVE_RIDE =              1;
    private int TYPE_HEADER =                  2;
    private boolean MY_RIDES_HEADER_TAG =       true;
    private boolean MY_ACTIVE_RIDES_HEADER_TAG  = false;
    private List<Object> rides;
    private final MainAct activity;
    private final List<RideRequestReceived> rideRequestReceivedList;
    private final List<NewChatMsgIndicator> newChatMsgIndicatorList;
//    private int positionMyRideHeaderPosition = 0;
//    private int positionMyActiveRideHeaderPosition;

    public MyRidesAdapter(List<Object> rides, MainAct activity) {
        this.activity = activity;

        rideRequestReceivedList = RideRequestReceived.listAll(RideRequestReceived.class);

        newChatMsgIndicatorList = NewChatMsgIndicator.listAll(NewChatMsgIndicator.class);

//        rides.add(positionMyRideHeaderPosition, true);
//
//        for (int rideCounter = 0; rideCounter < rides.size(); rideCounter++){
//            if (rides.get(rideCounter).getClass() == RideForJson.class){
//                rides.add(rideCounter, false);
//                positionMyActiveRideHeaderPosition = rideCounter;
//                break;
//            }
//        }

        this.rides = rides;
    }

    @Override
    public MyRidesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView;

        if (viewType == TYPE_MY_RIDE) {
            contactView = inflater.inflate(R.layout.item_myride, parent, false);
        } else if (viewType == TYPE_ACTIVE_RIDE){
            contactView = inflater.inflate(R.layout.item_myactiveride, parent, false);
        } else {
            contactView = inflater.inflate(R.layout.list_separator, parent, false);
        }

        return new ViewHolder(contactView);
    }

    @Override
    public int getItemViewType(int position) {
        if (rides.get(position).getClass() == Ride.class){
            return TYPE_MY_RIDE;
        } else if(rides.get(position).getClass() == RideForJson.class) {
            return TYPE_ACTIVE_RIDE;
        } else
            return TYPE_HEADER;
    }

    @Override
    public void onBindViewHolder(final MyRidesAdapter.ViewHolder holder, final int position) {

        if (rides.get(position).getClass() == Ride.class) {

            configureMyOfferRide(position, holder);

        } else if(rides.get(position).getClass() == RideForJson.class) {
            configureMyActiveRides(position, holder);

        } else if (rides.get(position).getClass() == Boolean.class){
            boolean type = (boolean)rides.get(position);
            if (type) {
                holder.list_separator_text.setText(activity.getResources().getString(R.string.frag_myrides_title));
                holder.card_list_separator.setBackground(ContextCompat.getDrawable(activity, getRandomColor()));
                holder.list_separator_text.setTextColor(ContextCompat.getColor(activity, R.color.black));
            }
            else {
                holder.list_separator_text.setText(activity.getResources().getString(R.string.frag_myactiverides_title));
                holder.card_list_separator.setBackground(ContextCompat.getDrawable(activity, getRandomColor()));
                holder.list_separator_text.setTextColor(ContextCompat.getColor(activity, R.color.black));
            }
        }
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time_tv;
        public TextView location_tv;
        public TextView routine_tv;
        public TextView slots_tv;
        public TextView date_tv;
        public android.widget.ImageButton delete_bt;
        public android.widget.RelativeLayout layout;
        public ImageView newRequest_iv;

        public TextView name_tv;
        public ImageView newMsgIndicator_iv;
        public CircleImageView photo_iv;

        public android.widget.FrameLayout card_list_separator;
        public TextView list_separator_text;


        public ViewHolder(View itemView) {
            super(itemView);

            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            location_tv = (TextView) itemView.findViewById(R.id.location_tv);
            routine_tv = (TextView) itemView.findViewById(R.id.routine_tv);
            slots_tv = (TextView) itemView.findViewById(R.id.slots_tv);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            delete_bt = (android.widget.ImageButton) itemView.findViewById(R.id.delete_bt);
            layout = (android.widget.RelativeLayout) itemView.findViewById(R.id.cardView);
            newRequest_iv = (ImageView) itemView.findViewById(R.id.newRequest_iv);

            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            photo_iv = (CircleImageView) itemView.findViewById(R.id.photo_iv);
            newMsgIndicator_iv = (ImageView) itemView.findViewById(R.id.newMsgIndicator_iv);

            card_list_separator = (android.widget.FrameLayout) itemView.findViewById(R.id.card_list_separator);
            list_separator_text = (TextView) itemView.findViewById(R.id.list_separator_text);
        }
    }

    private int getRandomColor(){
        Random random = new Random();
        int color = random.nextInt(5);

        switch (color){
            case 0:
                return R.drawable.zone_centro_gradient;
            case 1:
                return R.drawable.zone_sul_gradient;
            case 2:
                return R.drawable.zone_oeste_gradient;
            case 3:
                return R.drawable.zone_baixada_gradient;
            case 4:
                return R.drawable.zone_niteroi_gradient;
            default:
                return R.drawable.zone_outros_gradient;
        }
    }

    private void configureMyOfferRide(int position, final MyRidesAdapter.ViewHolder holder){

        final Ride ride = (Ride)rides.get(position);

        Drawable background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_outros);
        int color = ContextCompat.getColor(activity, R.color.zone_outros);
        if (ride.getZone().equals("Centro")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_centro);
            color = ContextCompat.getColor(activity, R.color.zone_centro);
        }
        if (ride.getZone().equals("Zona Sul")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_sul);
            color = ContextCompat.getColor(activity, R.color.zone_sul);
        }
        if (ride.getZone().equals("Zona Oeste")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_oeste);
            color = ContextCompat.getColor(activity, R.color.zone_oeste);
        }
        if (ride.getZone().equals("Zona Norte")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_norte);
            color = ContextCompat.getColor(activity, R.color.zone_norte);
        }
        if (ride.getZone().equals("Baixada")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_baixada);
            color = ContextCompat.getColor(activity, R.color.zone_baixada);
        }
        if (ride.getZone().equals("Grande Niterói")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_niteroi);
            color = ContextCompat.getColor(activity, R.color.zone_niteroi);
        }
        holder.location_tv.setTextColor(color);

        if (ride.isGoing())
            holder.time_tv.setText(activity.getString(R.string.arrivingAt, ride.getTime()));
        else
            holder.time_tv.setText(activity.getString(R.string.leavingAt, ride.getTime()));
        holder.date_tv.setText(Util.formatGoodDateWithoutYear(ride.getDate()));
        holder.slots_tv.setText(activity.getString(R.string.Xslots, ride.getSlots(), (Integer.parseInt(ride.getSlots()) > 1 ? "s" : "")));
        String location;
        if (ride.isGoing())
            location = ride.getNeighborhood() + " ➜ " + ride.getHub();
        else
            location = ride.getHub() + " ➜ " + ride.getNeighborhood();
        holder.location_tv.setText(location);

        String s = activity.getString(R.string.repeats);
        if (ride.isRoutine()) {
            s += ride.getWeekDays().contains("7") ? activity.getString(R.string.sunday) : "";
            s += ride.getWeekDays().contains("1") ? activity.getString(R.string.monday) : "";
            s += ride.getWeekDays().contains("2") ? activity.getString(R.string.tuesday) : "";
            s += ride.getWeekDays().contains("3") ? activity.getString(R.string.wednesday) : "";
            s += ride.getWeekDays().contains("4") ? activity.getString(R.string.thursday) : "";
            s += ride.getWeekDays().contains("5") ? activity.getString(R.string.friday) : "";
            s += ride.getWeekDays().contains("6") ? activity.getString(R.string.saturday) : "";
            s = s.substring(0, s.length() - 1);
        } else {
            s = activity.getString(R.string.notRoutine);
        }
        holder.routine_tv.setText(s);

        holder.delete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ride.isRoutine()) {
                    Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

                        @Override
                        protected void onBuildDone(Dialog dialog) {
                            dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        }

                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            RadioGroup radioGroup = (RadioGroup) fragment.getDialog().findViewById(R.id.radioGroup);
                            int checkedRadioButton = radioGroup.getCheckedRadioButtonId();
                            switch (checkedRadioButton) {
                                case R.id.all_rb:
                                    deleteAllRidesFromRoutine();
                                    break;
                                case R.id.single_rb:
                                    deleteSingleRide();
                                    break;
                            }

                            super.onPositiveActionClicked(fragment);
                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    builder.title(activity.getString(R.string.attention))
                            .positiveAction(activity.getString(R.string.ok))
                            .negativeAction(activity.getString(R.string.cancel))
                            .contentView(R.layout.delete_routine_dialog);

                    DialogFragment fragment = DialogFragment.newInstance(builder);
                    fragment.show(activity.getSupportFragmentManager(), null);
                } else {
                    deleteSingleRide();
                }
            }

            private void deleteAllRidesFromRoutine() {
                Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        final ProgressDialog pd = ProgressDialog.show(activity, "", activity.getString(R.string.wait), true, true);
                        final String routineId = ride.getRoutineId();
                        App.getNetworkService(activity.getApplicationContext()).deleteAllRidesFromRoutine(routineId)
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            pd.dismiss();
                                            Util.toast(R.string.ridesDeleted);
                                            Iterator<Object> it = rides.iterator();
                                            while (it.hasNext()) {
                                                Object object = it.next();
                                                if (object.getClass() == Ride.class) {
                                                    Ride ride2 = (Ride)object;
                                                    if (ride2.getRoutineId().equals(routineId))
                                                        it.remove();
                                                }
                                            }
                                            notifyDataSetChanged();
                                            Ride.deleteAll(Ride.class, "routine_id = ?", routineId);
                                        } else {
                                            pd.dismiss();
                                            Util.toast(activity.getString(R.string.errorDeleteRide));
                                            Log.e("deleteAllFromRoutine", response.message());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        pd.dismiss();
                                        Util.toast(activity.getString(R.string.errorDeleteRide));
                                        Log.e("deleteAllFromRoutine", t.getMessage());
                                    }
                                });

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                ((SimpleDialog.Builder) builder).message(activity.getString(R.string.warnDeleteRidesCouldBeActive))
                        .title(activity.getString(R.string.attention))
                        .positiveAction(activity.getString(R.string.ok))
                        .negativeAction(activity.getString(R.string.cancel));

                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(activity.getSupportFragmentManager(), null);
            }

            private void deleteSingleRide() {
                Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        final ProgressDialog pd = ProgressDialog.show(activity, "", activity.getString(R.string.wait), true, true);
                        App.getNetworkService(activity.getApplicationContext()).deleteRide(ride.getDbId() + "")
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            pd.dismiss();
                                            Util.toast(R.string.rideDeleted);
                                            rides.remove(ride);
                                            notifyItemRemoved(holder.getAdapterPosition());
                                            ride.delete();
                                        } else {
                                            pd.dismiss();
                                            Util.toast(activity.getString(R.string.errorDeleteRide));
                                            Log.e("deleteRide", response.message());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        pd.dismiss();
                                        Util.toast(activity.getString(R.string.errorDeleteRide));
                                        Log.e("deleteRide", t.getMessage());

                                    }
                                });

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                ((SimpleDialog.Builder) builder).message(activity.getString(R.string.warnDeleteRideCouldBeActive))
                        .title(activity.getString(R.string.attention))
                        .positiveAction(activity.getString(R.string.ok))
                        .negativeAction(activity.getString(R.string.cancel));

                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(activity.getSupportFragmentManager(), null);
            }
        });

        final int colorToSend = color;
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd = ProgressDialog.show(activity, "", activity.getResources().getString(R.string.wait), true, true);

                RideRequestReceived.deleteAll(RideRequestReceived.class, "db_id = ?", ride.getDbId() + "");
                holder.newRequest_iv.setVisibility(View.INVISIBLE);

                App.getNetworkService(activity.getApplicationContext()).getRequesters(ride.getDbId() + "")
                        .enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                if (response.isSuccessful()) {
                                    pd.dismiss();
                                    List<User> users = response.body();

                                    if (users.isEmpty()) {
                                        Util.toast(R.string.noRequesters);
                                    } else {
                                        Intent intent = new Intent(activity, RequestersListAct.class);
                                        intent.putParcelableArrayListExtra("users", (ArrayList<User>) users);
                                        intent.putExtra("rideId", ride.getDbId());
                                        intent.putExtra("color", colorToSend);
                                        activity.startActivity(intent);
                                    }
                                } else {
                                    pd.dismiss();
                                    Util.toast(R.string.errorGetRequesters);
                                    Log.e("getRequesters", response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {
                                pd.dismiss();
                                Util.toast(R.string.errorGetRequesters);
                                Log.e("getRequesters", t.getMessage());
                            }
                        });


            }
        });

        boolean found = false;
        for (RideRequestReceived requestReceived : rideRequestReceivedList) {
            if (requestReceived.getDbId() == ride.getDbId()) {
                holder.newRequest_iv.setVisibility(View.VISIBLE);
                found = true;
                break;
            }
        }
        if (!found)
            holder.newRequest_iv.setVisibility(View.INVISIBLE);
    }

    private void configureMyActiveRides(int position, final MyRidesAdapter.ViewHolder holder){
        final RideForJson rideOffer = (RideForJson) rides.get(position);

        Drawable background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_outros);
        int color = ContextCompat.getColor(activity, R.color.zone_outros);
        if (rideOffer.getZone().equals("Centro")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_centro);
            color = ContextCompat.getColor(activity, R.color.zone_centro);
        }
        if (rideOffer.getZone().equals("Zona Sul")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_sul);
            color = ContextCompat.getColor(activity, R.color.zone_sul);
        }
        if (rideOffer.getZone().equals("Zona Oeste")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_oeste);
            color = ContextCompat.getColor(activity, R.color.zone_oeste);
        }
        if (rideOffer.getZone().equals("Zona Norte")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_norte);
            color = ContextCompat.getColor(activity, R.color.zone_norte);
        }
        if (rideOffer.getZone().equals("Baixada")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_baixada);
            color = ContextCompat.getColor(activity, R.color.zone_baixada);
        }
        if (rideOffer.getZone().equals("Grande Niterói")) {
            background = ContextCompat.getDrawable(activity, R.drawable.card_list_bg_zone_niteroi);
            color = ContextCompat.getColor(activity, R.color.zone_niteroi);
        }
        holder.location_tv.setTextColor(color);
        holder.photo_iv.setBorderColor(color);

        String profilePicUrl = rideOffer.getDriver().getProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Picasso.with(activity).load(profilePicUrl)
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation())
                    .into(holder.photo_iv);
        }

        if (App.getUser().getDbId() != rideOffer.getDriver().getDbId())
            holder.photo_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ProfileAct.class);
                    intent.putExtra("user", new Gson().toJson(rideOffer.getDriver()));
                    intent.putExtra("from", "myactiveridesadapter");
                    activity.startActivity(intent);
                }
            });

        String timeText;
        if (rideOffer.isGoing())
            timeText = activity.getResources().getString(R.string.arrivingAt, Util.formatTime(rideOffer.getTime()));
        else
            timeText = activity.getResources().getString(R.string.leavingAt, Util.formatTime(rideOffer.getTime()));
        holder.time_tv.setText(timeText);
        holder.date_tv.setText(Util.formatBadDateWithoutYear(rideOffer.getDate()));
        holder.name_tv.setText(rideOffer.getDriver().getName());
        String location;
        if (rideOffer.isGoing())
            location = rideOffer.getNeighborhood() + " ➜ " + rideOffer.getHub();
        else
            location = rideOffer.getHub() + " ➜ " + rideOffer.getNeighborhood();
        holder.location_tv.setText(location);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewChatMsgIndicator.deleteAll(NewChatMsgIndicator.class, "db_id = ?", rideOffer.getDbId()+"");
                holder.newMsgIndicator_iv.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(activity, ActiveRideAct.class);
                intent.putExtra("ride", rideOffer);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
            }
        });

        boolean found = false;
        for (NewChatMsgIndicator newChatMsgIndicator : newChatMsgIndicatorList) {
            if (newChatMsgIndicator.getDbId() == rideOffer.getDbId()) {
                holder.newMsgIndicator_iv.setVisibility(View.VISIBLE);
                found = true;
                break;
            }
        }

        if (!found)
            holder.newMsgIndicator_iv.setVisibility(View.INVISIBLE);
    }
}
