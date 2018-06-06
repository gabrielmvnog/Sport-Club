package br.com.gfsportclub.sportclub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


public class ProfileFragment extends Fragment {
    private ImageButton setup;
    private ImageView profileImage;
    private TextView nome ,interesses, fallowers, fallowing;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private User userOnline;
    private String interessesProfile = "";


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

        profileImage = (ImageView) v.findViewById(R.id.profilePicure);
        nome = (TextView) v.findViewById(R.id.profileUsername);
        interesses = (TextView) v.findViewById(R.id.interresesProfile);
        setup = (ImageButton) v.findViewById(R.id.profile_setup);
        fallowers = (TextView) v.findViewById(R.id.profile_fallowers);
        fallowing = (TextView) v.findViewById(R.id.profile_fallowing);

        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SetupActivity.class));
            }
        });

        fallowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FriendsActivity.class));
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userOnline = dataSnapshot.getValue(User.class);
                nome.setText(userOnline.getNome());
                Picasso.with(getContext()).load(userOnline.imagem).into(profileImage);

                for(DataSnapshot ds : dataSnapshot.child("esportes").getChildren()){
                    interessesProfile = interessesProfile + ds.getKey() + ", ";
                }

                if(interessesProfile.length() > 2) {
                    interessesProfile = interessesProfile.substring(0, interessesProfile.length() - 2);

                    interesses.setText(interessesProfile);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        // Inflate the layout for this fragment
        return v;
    }

}
