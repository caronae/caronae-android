package br.ufrj.caronae.frags.DialogFragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.adapters.SelectorListAdapter;
import br.ufrj.caronae.frags.RideOfferFrag;
import br.ufrj.caronae.frags.RideSearchFrag;

public class SelectorDialogFrag extends DialogFragment {

    RecyclerView rv;
    TextView dialogTitle;
    Button okButton;
    Button cancelButton;

    private static String[] textArray;
    private static int[] colorArray;
    private static boolean[] selectedItens;
    private static String title;
    private static android.support.v4.app.Fragment parentFragment;
    private static boolean isMultipleChoice;

    SelectorListAdapter adapter;

    public SelectorDialogFrag() {
        // Required empty public constructor
    }

    public static SelectorDialogFrag newInstance(String[] textArrayParam,
                                                 int[] colorArrayParam,
                                                 boolean[] selectedItensParam,
                                                 String titleParam,
                                                 android.support.v4.app.Fragment fragmentParam,
                                                 boolean isMultipleChoicesParam) {
        SelectorDialogFrag fragment = new SelectorDialogFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        textArray = textArrayParam;
        colorArray = colorArrayParam;
        selectedItens = selectedItensParam;
        title = titleParam;
        parentFragment = fragmentParam;
        isMultipleChoice = isMultipleChoicesParam;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_selector_dialog, new LinearLayout(getActivity()), false);

        rv = (RecyclerView) view.findViewById(R.id.rv_list);
        dialogTitle = (TextView) view.findViewById(R.id.toolbar_text);
        okButton = (Button) view.findViewById(R.id.ok_button);
        cancelButton = (Button) view.findViewById(R.id.cancel_button);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);

        Dialog builder = new Dialog(getActivity());

        if (!isMultipleChoice){
            okButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        }

        adapter = new SelectorListAdapter(this, textArray, colorArray, selectedItens, isMultipleChoice, title, parentFragment);
        rv.setAdapter(adapter);

        configureOkButton();
        configureCancelButton();

        dialogTitle.setText(title.toUpperCase());
        builder.setContentView(view);
        return builder;
    }

    private void configureOkButton() {
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] choices = adapter.getChoices();
                String result = "";
                if (!choices[0].equals("")) {
                    result = choices[0];
                } else {
                    for (int i = 1; i < choices.length; i++) {
                        if (!choices[i].equals(""))
                            result = result + "," + choices[i];
                    }
                    if (result.length() > 0)
                        result = result.substring(1, result.length());
                }
                SharedPref.saveDialogSearchPref(title, result);
                SharedPref.saveDialogTypePref(SharedPref.DIALOG_DISMISS_KEY, title);
                App.getBus().post(new RideSearchFrag());
                dismiss();
            }
        });
    }

    private void configureCancelButton() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPref.saveDialogSearchPref(title, "");
                SharedPref.saveDialogTypePref(SharedPref.DIALOG_DISMISS_KEY, title);
                App.getBus().post(new RideOfferFrag());
                dismiss();
            }
        });
    }
}
