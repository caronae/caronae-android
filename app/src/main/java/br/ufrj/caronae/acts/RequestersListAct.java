package br.ufrj.caronae.acts;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import br.ufrj.caronae.R;
import br.ufrj.caronae.adapters.RequestersAdapter;
import br.ufrj.caronae.models.User;
import butterknife.Bind;
import butterknife.ButterKnife;

public class RequestersListAct extends AppCompatActivity {

    @Bind(R.id.requestersList)
    RecyclerView requestersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requesters_list);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        ArrayList<User> users = getIntent().getExtras().getParcelableArrayList("users");
        int rideId = getIntent().getExtras().getInt("rideId");
        int color = getIntent().getExtras().getInt("color");

        requestersList.setAdapter(new RequestersAdapter(users, rideId, color, this));
        requestersList.setHasFixedSize(true);
        requestersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
