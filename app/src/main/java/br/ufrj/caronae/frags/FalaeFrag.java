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

import br.ufrj.caronae.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FalaeFrag extends Fragment {

    EditText reason_et;
    EditText subject_et;
    @BindView(R.id.message_et)
    EditText message_et;

    public static String selectedOption;
    public String reason_txt, subject_txt;
    boolean locked;

    public FalaeFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_falae, container, false);
        locked = false;
        subject_et = view.findViewById(R.id.subject_et);
        reason_et = view.findViewById(R.id.reason_et);
        ButterKnife.bind(this, view);
        selectedOption = "Reclamação";
        if(reason_txt != null)
        {
            if(!reason_txt.isEmpty())
            {
                setFalaeText();
                locked = true;
            }
        }
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
        return message_et.getText().toString();
    }

    @OnClick(R.id.reason_et)
    public void reason_et()
    {
        if(!locked) {
            final NumberPicker picker = new NumberPicker(getContext());
            String options[] = new String[]{"Reclamação", "Sugestão", "Denúncia", "Dúvida"};
            picker.setMinValue(0);
            picker.setMaxValue(options.length - 1);
            picker.setDisplayedValues(options);
            picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    selectedOption = ("" + options[newVal]);
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

    public void setFalaeText()
    {
        selectedOption = "Denúncia";
        subject_et.setKeyListener(null);
        subject_et.setPressed(false);
        subject_et.setFocusable(false);
        subject_et.setClickable(false);
        subject_et.setFocusableInTouchMode(false);
        subject_et.setText(subject_txt);
        reason_et.setKeyListener(null);
        reason_et.setPressed(false);
        reason_et.setFocusable(false);
        reason_et.setClickable(false);
        reason_et.setFocusableInTouchMode(false);
        reason_et.setText(reason_txt);
    }
}
