package br.com.gfsportclub.sportclub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class ProfileFragment extends Fragment {
    private ImageButton setup;
    private ImageView profileImage;
    private TextView nome ,interesses, friends, nfriends, cat, gen, dataNasc;
    private DatabaseReference mDatabase, dbUser, dbUserFoF;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private User userOnline;
    private Integer counter = 0;
    private String interessesProfile = "";
    private RecyclerView recyclerView;
    private List<String> listFoF = new LinkedList<>();
    private List<User> userFoF = new LinkedList<>();
    private List<String> visitados = new LinkedList<>();
    private Queue<String> queueFriends = new LinkedList<>();
    private UserAdapter userAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Perfil");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        dbUser = FirebaseDatabase.getInstance().getReference().child("users");
        dbUserFoF = FirebaseDatabase.getInstance().getReference().child("users");


        profileImage = (ImageView) v.findViewById(R.id.profilePicure);
        nome = (TextView) v.findViewById(R.id.profileUsername);
        interesses = (TextView) v.findViewById(R.id.interresesProfile);
        setup = (ImageButton) v.findViewById(R.id.profile_setup);
        friends = (TextView) v.findViewById(R.id.profile_friends_button);
        nfriends = (TextView) v.findViewById(R.id.profile_friends_counter);
        cat = (TextView) v.findViewById(R.id.profile_cat);
        gen = (TextView) v.findViewById(R.id.profile_gen);
        dataNasc = (TextView) v.findViewById(R.id.profile_data);

        recyclerView = (RecyclerView) v.findViewById(R.id.suggest_friends);

        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SetupActivity.class));
            }
        });

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                intent.putExtra("KEY", user.getUid());
                startActivity(intent);
            }
        });


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userOnline = dataSnapshot.getValue(User.class);
                nome.setText(userOnline.getNome());
                Picasso.with(getContext()).load(userOnline.imagem).into(profileImage);
                cat.setText(userOnline.getCategoria());
                gen.setText(userOnline.getGenero());
                dataNasc.setText(userOnline.getDatanasc());

                for(DataSnapshot ds : dataSnapshot.child("esportes").getChildren()){
                    interessesProfile = interessesProfile + ds.getKey() + ", ";
                }

                for(DataSnapshot ds : dataSnapshot.child("friends").getChildren()){
                    counter++;
                    queueFriends.add(ds.getKey());
                    visitados.add(ds.getKey());
                }

                BFS();


                nfriends.setText(counter.toString());

                if(interessesProfile.length() > 2) {
                    interessesProfile = interessesProfile.substring(0, interessesProfile.length() - 2);

                    interesses.setText(interessesProfile);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        userAdapter = new UserAdapter(getContext(), userFoF);
        recyclerView.setAdapter(userAdapter);

        RecyclerView.LayoutManager ll = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(ll);

        // Inflate the layout for this fragment
        return v;
    }

    public void BFS(){

        while(queueFriends.size() != 0 && userFoF.size() <= 10 ) {

            dbUser.child((String) queueFriends.poll()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.child("friends").getChildren()) {

                        if (!ds.getKey().equals(user.getUid()) && !visitados.contains(ds.getKey())) {

                            visitados.add(ds.getKey());
                            queueFriends.add(ds.getKey());

                            dbUserFoF.child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    User fof = dataSnapshot.getValue(User.class);
                                    userFoF.add(fof);
                                    userAdapter.notifyDataSetChanged();
                                    BFS();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

}
