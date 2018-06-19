package br.com.gfsportclub.sportclub;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventFragment extends Fragment {
    private DatabaseReference mDatabase, geoRef, userRef;
    private RecyclerView recyclerView;
    private final Calendar calendar = Calendar.getInstance();
    private TextView toolbar_title;
    private GeoFire geoFire;
    private List<Event> eventos = new ArrayList<>();
    private EventAdapter eventAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private List<String> esportes = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_event, container, false);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);

        toolbar_title = (TextView) v.findViewById(R.id.toolbar_title);
        toolbar_title.setText("Eventos");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("events");
        mDatabase.keepSynced(true);

        geoRef = FirebaseDatabase.getInstance().getReference().child("locations/events");
        geoFire = new GeoFire(geoRef);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User userOnline = dataSnapshot.getValue(User.class);

                for(DataSnapshot ds : dataSnapshot.child("esportes").getChildren()){
                    esportes.add(ds.getKey());
                }

                geoFireQuery(userOnline.getLat(), userOnline.getLng());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.eventList);

        eventAdapter = new EventAdapter(eventos, getActivity());
        recyclerView.setAdapter(eventAdapter);

        RecyclerView.LayoutManager ll = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(ll);


        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_event:
                getActivity().startActivity(new Intent(getActivity(), PostActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void geoFireQuery(double lat, double lng){

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lng), 15);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                
                mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final Event evento = dataSnapshot.getValue(Event.class);

                        if(esportes.contains(evento.getEsporte()) && evento.getTimestamp() >= calendar.getTimeInMillis()){
                            eventos.add(evento);
                        }

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

}
