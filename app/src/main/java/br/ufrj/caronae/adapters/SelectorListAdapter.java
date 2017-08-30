package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;

/**
 * Created by Luis on 8/30/2017.
 */

public class SelectorListAdapter extends RecyclerView.Adapter<SelectorListAdapter.ViewHolder> {

    private final short TEXT_COLUMN =           0;
    private final short COLOR_COLUMN =         1;

    Context context;


    String[][] textList;
    int[] colorIdList;

    /** Receive a String[][] with the texts and the color of the color bar with the model
     ** String[TEXT_COLUMN][text] and String[COLOR_COLUMN][R.color.xxxx]
     **/
    public SelectorListAdapter(String[][] textList){
        this.textList = textList;
        Util.makeColorIdList(textList[COLOR_COLUMN]);
    }

    @Override
    public SelectorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contentView = inflater.inflate(R.layout.selector_item, parent, false);

        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(SelectorListAdapter.ViewHolder holder, int position) {
        holder.text.setText(textList[TEXT_COLUMN][position]);
        holder.colorTab.setImageDrawable(context.getDrawable(colorIdList[position]));
    }

    @Override
    public int getItemCount() {
        return textList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView colorTab;
        private TextView text;
        private ImageView checkImage;
        public ViewHolder(View itemView) {
            super(itemView);

            colorTab = (ImageView)itemView.findViewById(R.id.color_bar);
            text = (TextView)itemView.findViewById(R.id.text);
            checkImage = (ImageView)itemView.findViewById(R.id.check_icon);
        }
    }
}
