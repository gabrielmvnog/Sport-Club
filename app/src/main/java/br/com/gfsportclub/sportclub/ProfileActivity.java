package br.com.gfsportclub.sportclub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvName, tvData, tvGen, tvCat, tvInt, tvCounter, tvFriends;
    private ImageView ivProfile;
    private DatabaseReference mDatabase, friendRequest;
    private String key, interessesProfile;
    private Integer counter = 0;
    private Button addButton;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText("Perfil");

        key = getIntent().getStringExtra("KEY");

        tvName = (TextView) findViewById(R.id.profile_activity_username);
        tvData = (TextView) findViewById(R.id.profile_activity_data);
        tvGen = (TextView) findViewById(R.id.profile_activity_gen);
        tvCat = (TextView) findViewById(R.id.profile_activity_cat);
        tvInt = (TextView) findViewById(R.id.profile_activity_int);
        tvCounter = (TextView) findViewById(R.id.profile_activity_counter);
        tvFriends = (TextView) findViewById(R.id.profile_activity_friends);
        ivProfile = (ImageView) findViewById(R.id.profile_activity_pic);
        addButton = (Button) findViewById(R.id.profile_activity_add);

        tvFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FriendsActivity.class);
                intent.putExtra("KEY", key);
                startActivity(intent);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                tvName.setText(user.getNome());
                tvData.setText(user.getDatanasc());
                tvGen.setText(user.getGenero());
                tvCat.setText(user.getCategoria());
                Picasso.with(ProfileActivity.this).load(user.imagem).into(ivProfile);

                for(DataSnapshot ds : dataSnapshot.child("esportes").getChildren()){
                    if(TextUtils.isEmpty(interessesProfile)){
                        interessesProfile = ds.getKey() + ", ";
                    } else {
                        interessesProfile = interessesProfile + ds.getKey() + ", ";
                    }
                }

                for(DataSnapshot ds : dataSnapshot.child("friends").getChildren()){
                    counter++;
                }

                tvCounter.setText(counter.toString());


                if(interessesProfile.length() > 0) {
                    interessesProfile = interessesProfile.substring(0, interessesProfile.length() - 2);

                    tvInt.setText(interessesProfile);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getUid().equals(key)) {
                    friendRequest = mDatabase.child("friends_request").push();
                    friendRequest.child(key).setValue("true");
                    friendRequest.child(user.getUid()).setValue("true");
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
