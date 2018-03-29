package br.com.gfsportclub.sportclub;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private Button cancelButton, registerButton;
    private EditText edtEmail, edtSenha;
    private DatabaseReference mDatabase;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        edtEmail = (EditText) findViewById(R.id.edtEmailRegister);
        edtSenha = (EditText) findViewById(R.id.edtSenhaRegister);
        cancelButton = (Button) findViewById(R.id.cancel_button_r);
        registerButton = (Button) findViewById(R.id.register_button);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    final String userEmail, userSenha;

                    userEmail = edtEmail.getText().toString().trim();
                    userSenha = edtSenha.getText().toString().trim();

                    if(TextUtils.isEmpty(userEmail)){
                        Toast.makeText(getApplicationContext(),"Entre com um email!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(TextUtils.isEmpty(userSenha)){
                        Toast.makeText(getApplicationContext(),"Entre com uma senha!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(userSenha.length() < 6){
                        Toast.makeText(getApplicationContext(),"A senha é muito curta!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userSenha)){



                        mAuth.createUserWithEmailAndPassword(userEmail,userSenha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                                    mDatabase.child("users").child(mAuth.getUid()).child("email").setValue(userEmail);
                                    startActivity(new Intent(RegisterActivity.this, SelectSportActivity.class));

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Falha ao criar conta", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Preencha os campos acima.", Toast.LENGTH_SHORT);
                    }
            }
        });

    }
}
