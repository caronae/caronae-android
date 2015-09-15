package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.models.User;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileFrag extends Fragment {
    @Bind(R.id.name_et)
    EditText name_et;
    @Bind(R.id.profile_et)
    EditText profile_et;
    @Bind(R.id.course_et)
    EditText course_et;
    @Bind(R.id.unit_et)
    EditText unit_et;
    @Bind(R.id.zone_et)
    EditText zone_et;
    @Bind(R.id.neighborhood_et)
    EditText neighborhood_et;
    @Bind(R.id.phoneNumber_et)
    EditText phoneNumber_et;
    @Bind(R.id.email_et)
    EditText email_et;
    @Bind(R.id.carOwner_cb)
    CheckBox carOwner_cb;
    @Bind(R.id.carModel_et)
    EditText carModel_et;
    @Bind(R.id.carColor_et)
    EditText carColor_et;
    @Bind(R.id.carPlate_et)
    EditText carPlate_et;

    public ProfileFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        User user = App.getUser();
        if (user != null) {
            name_et.setText(user.getName());
            profile_et.setText(user.getProfile());
            course_et.setText(user.getCourse());
            unit_et.setText(user.getUnit());
            zone_et.setText(user.getZone());
            neighborhood_et.setText(user.getNeighborhood());
            phoneNumber_et.setText(user.getPhoneNumber());
            email_et.setText(user.getEmail());
            carOwner_cb.setChecked(user.isCarOwner());
            carModel_et.setText(user.getCarModel());
            carColor_et.setText(user.getCarColor());
            carPlate_et.setText(user.getCarPlate());
        }

        return view;
    }

    @OnClick(R.id.save_bt)
    public void saveBt() {
        User user = App.getUser();
        final User editedUser = new User();
        editedUser.setName(name_et.getText().toString());
        editedUser.setProfile(profile_et.getText().toString());
        editedUser.setCourse(course_et.getText().toString());
        editedUser.setUnit(unit_et.getText().toString());
        editedUser.setZone(zone_et.getText().toString());
        editedUser.setNeighborhood(neighborhood_et.getText().toString());
        editedUser.setPhoneNumber(phoneNumber_et.getText().toString());
        editedUser.setEmail(email_et.getText().toString());
        editedUser.setCarOwner(carOwner_cb.isChecked());
        editedUser.setCarModel(carModel_et.getText().toString());
        editedUser.setCarColor(carColor_et.getText().toString());
        editedUser.setCarPlate(carPlate_et.getText().toString());

        if (!user.equals(editedUser)) {
            App.getApiaryService().updateUser(editedUser, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    User user = App.getUser();
                    user.setUser(editedUser);
                    user.save();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(App.LOGTAG, error.getMessage());
                }
            });
        }
    }
}
