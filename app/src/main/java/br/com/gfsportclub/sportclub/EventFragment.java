package br.com.gfsportclub.sportclub;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class EventFragment extends Fragment {
    private FloatingActionButton addPost;
    private DatabaseReference mDatabase;
    private RecyclerView mPeopleRV;
    private FirebaseRecyclerAdapter<Event, EventFragment.EventViewHolder> mPeopleRVAdapter;
    private final Calendar calendar = Calendar.getInstance();
    private TextView toolbar_title;

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

        toolbar_title = (TextView) v.findViewById(R.id.toolbar_title);;
        toolbar_title.setText("Eventos");

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("events");
        mDatabase.keepSynced(true);

        mPeopleRV = (RecyclerView) v.findViewById(R.id.eventList);

        DatabaseReference personsRef =  FirebaseDatabase.getInstance().getReference().child("events");
        Query personsQuery = personsRef.orderByKey();

        mPeopleRV.hasFixedSize();
        mPeopleRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Event>().setQuery(personsQuery, Event.class).build();

        mPeopleRVAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(personsOptions) {


            @Override
            protected void onBindViewHolder(EventFragment.EventViewHolder holder, final int position, final Event model) {

                if(model.getTimestamp() >= calendar.getTimeInMillis()) {

                    holder.setTitle(model.getTitulo());
                    holder.setData(model.getData() + " - " + model.getHora());
                    holder.setLocal(model.getNomeLocal());
                    holder.setSport(model.getEsporte());

                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), EventInfoActivity.class);
                            intent.putExtra("EVENT_KEY", model.getKey());
                            startActivity(intent);
                        }
                    });
                } else {
                    holder.itemView.setVisibility(holder.itemView.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

            }

            @Override
            public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_row, parent, false);

                return new EventFragment.EventViewHolder(view);
            }

        };

        mPeopleRV.setAdapter(mPeopleRVAdapter);


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

    @Override
    public void onStart() {
        super.onStart();
        mPeopleRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPeopleRVAdapter.stopListening();

    }

    public static class EventViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public EventViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.card_event_title);
            post_title.setText(title);
        }
        public void setData(String desc){
            TextView post_data = (TextView)mView.findViewById(R.id.card_event_data);
            post_data.setText(desc);
        }

        public void setLocal(String local) {
            TextView post_local = (TextView) mView.findViewById(R.id.card_event_location);
            post_local.setText(local);
        }

        public  void setSport(String sport){
            TextView post_sport = (TextView) mView.findViewById(R.id.card_event_sport);
            post_sport.setText(sport);
        }
    }



}
