package br.com.gfsportclub.sportclub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventSearchActivity extends AppCompatActivity {
    private DatabaseReference mDatabase, geoFireRef;
    private String sport;
    private List<Event> eventos = new ArrayList<>();
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private Calendar calendar = Calendar.getInstance();
    private GeoFire geoFire;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_search);

        recyclerView = (RecyclerView) findViewById(R.id.event_search_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sport = getIntent().getStringExtra("SPORT");
        Log.d("Key", sport);


        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(sport.toUpperCase());

        mDatabase = FirebaseDatabase.getInstance().getReference();

        geoFireRef = FirebaseDatabase.getInstance().getReference().child("locations/events");
        geoFire = new GeoFire(geoFireRef);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userOn = dataSnapshot.getValue(User.class);

                geoQuery(userOn.getLat(),userOn.getLng());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        eventAdapter = new EventAdapter(eventos, this);
        recyclerView.setAdapter(eventAdapter);

        RecyclerView.LayoutManager ll = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(ll);

    }

    public void geoQuery(double lat, double lng){

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat,lng), 50);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                mDatabase.child("events").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event event = dataSnapshot.getValue(Event.class);

                        if(event.getTimestamp() >= calendar.getTimeInMillis() && event.getEsporte().equals(sport)){
                            eventos.add(event);
                        }

                        quickSort(eventos, 0, (eventos.size() - 1));

                        eventAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    public int partitionQuickSort(List<Event> lEventos, int primeiro, int ultimo){
        double pivot = lEventos.get(ultimo).getTimestamp();
        int i = (primeiro - 1);

        for(int j = primeiro; j < ultimo; j++){

            if(lEventos.get(j).getTimestamp() <= pivot){
                i++;

                Event temp = lEventos.get(i);
                lEventos.set(i, lEventos.get(j));
                lEventos.set(j, temp);

            }

        }

        Event temp = lEventos.get(i + 1);
        lEventos.set(i + 1, lEventos.get(ultimo));
        lEventos.set(ultimo, temp);

        return i+1;
    }

    public void quickSort(List<Event> lEventos,int primeiro, int ultimo){

        if(primeiro < ultimo){

            int i = partitionQuickSort(lEventos, primeiro, ultimo);

            quickSort(lEventos, primeiro, i-1);
            quickSort(lEventos, i+1, ultimo);

        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
