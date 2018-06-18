package br.com.gfsportclub.sportclub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventSearchActivity extends AppCompatActivity {
    private DatabaseReference esportesDatabase, eventoDatabase;
    private String sport;
    private List<Event> eventos = new ArrayList<>();
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private Calendar calendar = Calendar.getInstance();

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

        esportesDatabase = FirebaseDatabase.getInstance().getReference().child("sports").child(sport).child("events");
        eventoDatabase = FirebaseDatabase.getInstance().getReference().child("events");

        esportesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    eventoDatabase.child(ds.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Event evento = dataSnapshot.getValue(Event.class);

                            if(evento.getTimestamp() >= calendar.getTimeInMillis()){
                                eventos.add(evento);
                            }

                            eventAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }


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

}
