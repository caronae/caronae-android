package br.ufrj.caronae.frags.DialogFragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import br.ufrj.caronae.R;
import br.ufrj.caronae.adapters.SelectorListAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectorDialogFrag extends DialogFragment {

    RecyclerView rv;

    private static String[] textArray;
    private static int[] colorArray;
    private static boolean[] selectedItens;

    public SelectorDialogFrag() {
        // Required empty public constructor
    }

    public static SelectorDialogFrag newInstance(String[] textArrayParam, int[] colorArrayParam, boolean[] selectedItensParam) {
        SelectorDialogFrag fragment = new SelectorDialogFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        textArray = textArrayParam;
        colorArray = colorArrayParam;
        selectedItens = selectedItensParam;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_selector_dialog, new LinearLayout(getActivity()), false);

        rv = (RecyclerView) view.findViewById(R.id.rv_list);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);

        SelectorListAdapter adapter = new SelectorListAdapter(textArray, colorArray, selectedItens, false);
        rv.setAdapter(adapter);

        Dialog builder = new Dialog(getActivity());
//        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setContentView(view);
        return builder;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
