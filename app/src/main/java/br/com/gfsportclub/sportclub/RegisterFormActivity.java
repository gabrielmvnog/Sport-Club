package br.com.gfsportclub.sportclub;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterFormActivity extends AppCompatActivity {
    private Spinner categoria, genero;
    private static final String[] generos = {"Masculino", "Feminino"};
    private static final String[] categorias = {"Profissional", "Intermediário", "Amador"};
    private int DIALOG_ID = 10;
    private EditText edtDate, edtApelido, edtNome;
    private int year_x, month_x, day_x;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Button finalizar;
    private String senha, email;
    private static final String TAG = "SignInActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        senha = getIntent().getStringExtra("SENHA");
        email = getIntent().getStringExtra("EMAIL");

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "signInWithEmail:success");
                    user = mAuth.getCurrentUser();
                    Log.d("UID", user.getUid());

                }   else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterFormActivity.this, "A autenticação falhou.",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });



        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");


        categoria = (Spinner) findViewById(R.id.reg_form_categoria);
        genero = (Spinner) findViewById(R.id.reg_form_genero);
        edtDate = (EditText) findViewById(R.id.reg_form_data_nasc);
        finalizar = (Button) findViewById(R.id.reg_form_button);
        edtApelido = (EditText) findViewById(R.id.reg_form_apelido);
        edtNome = (EditText) findViewById(R.id.reg_form_nome);


        ArrayAdapter<String> adapterGenero = new ArrayAdapter<>(RegisterFormActivity.this, android.R.layout.simple_spinner_dropdown_item, generos);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(RegisterFormActivity.this, android.R.layout.simple_spinner_dropdown_item, categorias);

        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoria.setAdapter(adapterCategoria);
        genero.setAdapter(adapterGenero);

        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        genero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String generoUser, dateUser, categoriaUser, apelidoUser, nomeUser;
                generoUser = genero.toString();
                dateUser = edtDate.getText().toString().trim();
                categoriaUser = categoria.toString();
                apelidoUser = edtApelido.getText().toString().trim();
                nomeUser = edtNome.getText().toString().trim();

                if(TextUtils.isEmpty(apelidoUser)){
                    Toast.makeText(getApplicationContext(),"Entre com um nome de usuário!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(nomeUser)){
                    Toast.makeText(getApplicationContext(),"Entre com um nome!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(dateUser)){
                    Toast.makeText(getApplicationContext(),"Entre com uma data de nascimento!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(!TextUtils.isEmpty(apelidoUser) && !TextUtils.isEmpty(dateUser) && !TextUtils.isEmpty(nomeUser)){
                    checkUserExist(apelidoUser);
                } else {
                    Toast.makeText(getApplicationContext(), "Preencha os campos acima.", Toast.LENGTH_SHORT);
                }


            }
        });



    }

    private DatePickerDialog.OnDateSetListener dpickerListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month;
            day_x = dayOfMonth;
            edtDate.setText(day_x + "/" + month_x + "/" + year_x);
        }
    };


    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG_ID) return new DatePickerDialog(this, dpickerListner, year_x, month_x, day_x);

        return null;

    }


    public void checkUserExist(String username){
        boolean check = false;
        Query query = mDatabase.child("users").orderByChild("username").equalTo(username);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    Toast.makeText(getApplicationContext(),"Nome de usuário já cadastrado!", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(RegisterFormActivity.this, SelectSportActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }






}
