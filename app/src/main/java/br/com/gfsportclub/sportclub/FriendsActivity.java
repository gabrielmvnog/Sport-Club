package br.com.gfsportclub.sportclub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference dbUser, dbFriends;
    List<User> friends;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText("Amigos");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        friends = new ArrayList<>();

        key = getIntent().getStringExtra("KEY");

        recyclerView = (RecyclerView) findViewById(R.id.friends_recyclerview);

        dbUser = FirebaseDatabase.getInstance().getReference().child("users");
        dbFriends = FirebaseDatabase.getInstance().getReference().child("users").child(key).child("friends");

        dbFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    dbUser.child(ds.getKey()).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User friend = dataSnapshot.getValue(User.class);

                            friends.add(friend);

                            userAdapter.notifyDataSetChanged();
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



        userAdapter = new UserAdapter(FriendsActivity.this, friends);
        recyclerView.setAdapter(userAdapter);

        GridLayoutManager gl = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gl);


    }

}


