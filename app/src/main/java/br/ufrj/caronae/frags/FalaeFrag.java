package br.ufrj.caronae.frags;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FalaeFrag extends Fragment {


    @BindView(R.id.subject_et)
    EditText subject_et;
    @BindView(R.id.message_et)
    EditText message_et;
    @BindView(R.id.reason_et)
    EditText reason_et;

    public static String selectedOption;

    public FalaeFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        selectedOption = "Reclamação";
        View view = inflater.inflate(R.layout.fragment_falae, container, false);
        ButterKnife.bind(this, view);
        User user = App.getUser();
        return view;
    }

    public String getSubject()
    {
        String subject;
        subject = "["+selectedOption+"] ";
        if(!subject_et.getText().toString().isEmpty())
        {
            subject += subject_et.getText().toString();
        }
        return subject;
    }

    public String getMessage()
    {
        String message = message_et.getText().toString();
        return message;
    }

    @OnClick(R.id.reason_et)
    public void reason_et()
    {
        final NumberPicker picker = new NumberPicker(getContext());
        String options[] = new String[]{"Reclamação", "Sugestão", "Denúncia", "Dúvida"};
        picker.setMinValue(0);
        picker.setMaxValue(options.length-1);
        picker.setDisplayedValues(options);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedOption= ("" + options[newVal]);
            }
        });
        final FrameLayout layout = new FrameLayout(getContext());
        layout.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM));

        new AlertDialog.Builder(getContext())
            .setTitle("Qual o motivo do seu contato?")
            .setView(layout)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    reason_et.setHint(selectedOption);
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }
}
