package br.com.gfsportclub.sportclub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class EventInfoActivity extends AppCompatActivity {
    private String eventKey;
    private DatabaseReference mDatabase;
    private TextView txtLocal, txtTitle, txtDescr, txtData;
    private ImageView imgEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventKey = getIntent().getStringExtra("EVENT_KEY");

        txtLocal = (TextView) findViewById(R.id.event_info_local);
        txtTitle = (TextView) findViewById(R.id.event_info_title);
        txtDescr = (TextView) findViewById(R.id.event_info_descr);
        txtData = (TextView) findViewById(R.id.event_info_time);
        imgEvent = (ImageView) findViewById(R.id.event_info_image);

        mDatabase = FirebaseDatabase.getInstance().getReference("events").child(eventKey);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);

                txtLocal.setText(event.getLocal());
                txtTitle.setText(event.getTitulo());
                txtDescr.setText(event.getDescr());
                txtData.setText(event.getData());

                Picasso.with(EventInfoActivity.this).load(event.getImagem()).into(imgEvent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
