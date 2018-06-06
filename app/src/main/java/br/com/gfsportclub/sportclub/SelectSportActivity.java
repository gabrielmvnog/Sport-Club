package br.com.gfsportclub.sportclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SelectSportActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button btnFinish;
    private SelectSportAdapter selectSportAdapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sport);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("esportes");

        recyclerView = (RecyclerView) findViewById(R.id.selectsport_recyclerview);

        selectSportAdapter = new SelectSportAdapter(this, mDatabase);
        recyclerView.setAdapter(selectSportAdapter);

        GridLayoutManager gl = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gl);

        btnFinish = (Button) findViewById(R.id.btnSportSelect);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectSportActivity.this, BNActivity.class));
            }
        });

    }

}
