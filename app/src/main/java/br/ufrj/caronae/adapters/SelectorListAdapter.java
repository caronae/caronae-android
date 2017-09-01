package br.ufrj.caronae.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.frags.DialogFragment.SelectorDialogFrag;

/**
 * Created by Luis on 8/30/2017.
 */

public class SelectorListAdapter extends RecyclerView.Adapter<SelectorListAdapter.ViewHolder> {

    private final short TEXT_COLUMN = 0;
    private final short COLOR_COLUMN = 1;

    String[] textList;
    int[] colorIdList;
    boolean[] selectedItens;
    boolean isMultipleChoice;
    SelectorDialogFrag dialog;
    String type;
    Context context;
    Fragment fragment;
    String[] choices;

    /**
     * Receive a String[] with the texts and a int[] with the colors R.color.xxx, if values of color id
     * * is null or the color array is shorter than text array the color will be set transparent and check will
     * * be set to darker_gray
     **/
    public SelectorListAdapter(SelectorDialogFrag frag,
                               String[] textList,
                               int[] colorIdList,
                               boolean[] selectedItens,
                               boolean isMultipleChoices,
                               String type,
                               Fragment fragment) {
        this.textList = textList;
        this.colorIdList = colorIdList;
        this.selectedItens = selectedItens;
        this.isMultipleChoice = isMultipleChoices;
        this.dialog = frag;
        this.fragment = fragment;
        this.type = type;

        if (isMultipleChoices) {
            choices = new String[textList.length];
            for (int i = 0; i < choices.length; i++)
                choices[i] = "";
        }

    }

    @Override
    public SelectorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contentView = inflater.inflate(R.layout.selector_item, parent, false);

        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(final SelectorListAdapter.ViewHolder holder, final int position) {
        holder.text.setText(textList[position]);
        if (position < colorIdList.length) {
            holder.colorTab.setBackgroundColor(colorIdList[position]);
        } else {
            holder.colorTab.setBackgroundColor(context.getColor(android.R.color.transparent));
        }

        if (isMultipleChoice) {
            if (position < colorIdList.length) {
                holder.checkImage.setColorFilter(colorIdList[position]);
            } else {
                holder.checkImage.setColorFilter(context.getColor(android.R.color.darker_gray));
            }
            updateCheckerVisibility(holder.checkImage, selectedItens[position]);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedItens[position] = !selectedItens[position];
                    updateCheckerVisibility(holder.checkImage, selectedItens[position]);
                    if (choices[position].equals(""))
                        choices[position] = textList[position];
                    else
                        choices[position] = "";
                }
            });
        } else {
            holder.checkImage.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
            holder.checkImage.setColorFilter(context.getColor(android.R.color.darker_gray));
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPref.saveDialogSearchPref(type, textList[position]);
                    setKeyByType();
                    App.getBus().post(fragment);
                    dialog.dismiss();
                }
            });
        }
    }

    private void setKeyByType() {
        SharedPref.saveDialogTypePref(SharedPref.DIALOG_DISMISS_KEY, type);
    }

    private static void updateCheckerVisibility(ImageView checker, boolean visible) {
        if (visible) {
            checker.setVisibility(View.VISIBLE);
            return;
        }
        checker.setVisibility(View.INVISIBLE);
    }

    public String[] getChoices(){
        return choices;
    }

    @Override
    public int getItemCount() {
        return textList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView colorTab;
        private TextView text;
        private ImageView checkImage;
        RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);

            colorTab = (ImageView) itemView.findViewById(R.id.color_bar);
            text = (TextView) itemView.findViewById(R.id.text);
            checkImage = (ImageView) itemView.findViewById(R.id.check_icon);
            layout = (RelativeLayout) itemView.findViewById(R.id.main_layout);
        }
    }
}
