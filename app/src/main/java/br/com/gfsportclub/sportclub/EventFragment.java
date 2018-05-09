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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class EventFragment extends Fragment {
    private FloatingActionButton addPost;
    private DatabaseReference mDatabase;
    private RecyclerView mPeopleRV;
    private FirebaseRecyclerAdapter<Event, EventFragment.EventViewHolder> mPeopleRVAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Eventos");
        setHasOptionsMenu(true);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_event, container, false);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);

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
                holder.setTitle(model.getTitulo());
                holder.setData(model.getData());
                holder.setLocal(model.getLocal());
                holder.setImage(getContext(), model.getImagem());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), EventInfoActivity.class);
                        intent.putExtra("EVENT_KEY", model.getKey());
                        startActivity(intent);
                    }
                });

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
        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.card_event_image);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setLocal(String local) {
            TextView post_local = (TextView) mView.findViewById(R.id.card_event_location);
            post_local.setText(local);
        }
    }



}
