package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.R;
import br.ufrj.caronae.models.modelsforjson.PlacesForJson;

public class PlaceAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> placeList;

    public PlaceAdapter() {
        placeList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = null;

        contactView = inflater.inflate(R.layout.custom_zonebar, parent, false);
        return new ViewHolder(contactView);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(placeList == null || placeList.size() == 0)) {
            if (placeList.get(position).getClass().equals(PlacesForJson.class)) {

            }
        }
    }

    @Override
    public int getItemCount() {
        if (placeList == null || placeList.size() == 0) {
            return 1;
        }
        return placeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainLayout;
        ImageView bar_iv;
        TextView bar_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.main_layout);
            bar_iv = itemView.findViewById(R.id.bar_iv);
            bar_tv = itemView.findViewById(R.id.bar_tv);
        }
    }
}
