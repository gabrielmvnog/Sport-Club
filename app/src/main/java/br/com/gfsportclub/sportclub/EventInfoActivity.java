package br.com.gfsportclub.sportclub;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class EventInfoActivity extends AppCompatActivity {
    private String eventKey;
    private DatabaseReference mDatabase, userDatabase, sportDb, geoRef;
    private TextView txtLocal, txtTitle, txtDescr, txtData, txtSport;
    private Button edtButton, joinButton, deleteButton;
    private RecyclerView recyclerView;
    private List<User> users = new ArrayList<>();
    private UserAdapter userAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ValueEventListener mListener;

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.addValueEventListener(mListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText("Evento");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        eventKey = getIntent().getStringExtra("EVENT_KEY");

        txtLocal = (TextView) findViewById(R.id.event_info_local);
        txtTitle = (TextView) findViewById(R.id.event_info_title);
        txtDescr = (TextView) findViewById(R.id.event_info_descr);
        txtData = (TextView) findViewById(R.id.event_info_time);
        txtSport = (TextView) findViewById(R.id.event_info_sport);

        edtButton = (Button) findViewById(R.id.event_info_edit);
        joinButton = (Button) findViewById(R.id.event_info_join);
        deleteButton = (Button) findViewById(R.id.event_info_delete);

        userDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference("events").child(eventKey);
        geoRef = FirebaseDatabase.getInstance().getReference().child("locations/events").child(eventKey);


         mListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Event event = dataSnapshot.getValue(Event.class);

                txtLocal.setText(event.getNomeLocal());
                txtTitle.setText(event.getTitulo());
                txtDescr.setText(event.getDescr());
                txtData.setText(event.getData() + " - " + event.getHora());
                txtSport.setText(event.getEsporte());

                edtButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EventInfoActivity.this, PostActivity.class);
                        intent.putExtra("EVENT_KEY", event.getKey());
                        startActivity(intent);
                    }
                });


                joinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!dataSnapshot.child("users").child(user.getUid()).exists()){
                            users.clear();
                            mDatabase.child("users").child(user.getUid()).setValue("true");
                        } else{
                            users.clear();
                            mDatabase.child("users").child(user.getUid()).setValue(null);
                        }
                    }
                });


                if(!event.getAdm().equals(user.getUid())){
                   edtButton.setVisibility(View.GONE);
                   deleteButton.setVisibility(View.GONE);
                }

                for(DataSnapshot user : dataSnapshot.child("users").getChildren()){
                    listarUsuario(user.getKey());
                }

                sportDb = FirebaseDatabase.getInstance().getReference("sports").child(event.getEsporte()).child("events").child(eventKey);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(EventInfoActivity.this)
                                .setTitle("Deletar")
                                .setMessage("Você tem certeza que deseja deletar?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        sportDb.setValue(null);
                                        geoRef.setValue(null);
                                        mDatabase.setValue(null);
                                        startActivity(new Intent(EventInfoActivity.this,BNActivity.class));
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                });

                userAdapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };



        recyclerView = (RecyclerView) findViewById(R.id.event_info_players);

        userAdapter = new UserAdapter(this, users);
        recyclerView.setAdapter(userAdapter);

        RecyclerView.LayoutManager ll = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(ll);


    }

    @Override
    protected void onStop() {
        super.onStop();
        mDatabase.removeEventListener(mListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabase.removeEventListener(mListener);
    }

    public void listarUsuario(String key){

        userDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                users.add(user);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
