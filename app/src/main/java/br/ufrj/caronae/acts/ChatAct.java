package br.ufrj.caronae.acts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import br.ufrj.caronae.R;

public class ChatAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String rideId = getIntent().getExtras().getString("rideId");
        Log.i("ChatAct", "rideId = " + rideId);
    }

}
