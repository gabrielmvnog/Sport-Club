package br.com.gfsportclub.sportclub;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetupActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Button logout;
    private TextView nomeUsu, dataNasc;
    private Spinner genero, categoria;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private static final String[] generos = {"Masculino", "Feminino"};
    private static final String[] categorias = {"Profissional", "Intermediário", "Amador"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        ArrayAdapter<String> adapterGenero = new ArrayAdapter<>(SetupActivity.this, android.R.layout.simple_spinner_dropdown_item, generos);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(SetupActivity.this, android.R.layout.simple_spinner_dropdown_item, categorias);

        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());

        logout = (Button) findViewById(R.id.settings_logout);
        nomeUsu = (TextView) findViewById(R.id.profile_setup_name);
        dataNasc = (TextView) findViewById(R.id.profile_setup_data);
        genero = (Spinner) findViewById(R.id.profile_setup_genero);
        categoria = (Spinner) findViewById(R.id.profile_setup_categoria);

        categoria.setAdapter(adapterCategoria);
        genero.setAdapter(adapterGenero);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user != null){
                    nomeUsu.setText(user.nome);
                    dataNasc.setText(user.getDatanasc());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                startActivity(new Intent(SetupActivity.this, LoginActivity.class));
            }
        });



    }

    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        LoginManager.getInstance().logOut();
    }
}
