package br.com.gfsportclub.sportclub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_event, container, false);


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
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDescr());
                holder.setImage(getContext(), model.getImage());

            }

            @Override
            public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_row, parent, false);

                return new EventFragment.EventViewHolder(view);
            }

        };

        mPeopleRV.setAdapter(mPeopleRVAdapter);

        addPost = (FloatingActionButton) v.findViewById(R.id.addButton);


        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PostActivity.class));
            }
        });

        return v;
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
            TextView post_title = (TextView)mView.findViewById(R.id.event_title);
            post_title.setText(title);
        }
        public void setDesc(String desc){
            TextView post_desc = (TextView)mView.findViewById(R.id.event_descr);
            post_desc.setText(desc);
        }
        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.event_image);
            Picasso.with(ctx).load(image).into(post_image);
        }

    }



}
