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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {
    private Button cancelButton, sendEmail;
    private EditText emailUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        cancelButton = (Button) findViewById(R.id.cancel_button_f);
        sendEmail = (Button) findViewById(R.id.enviar_f);
        emailUser = (EditText) findViewById(R.id.email_f);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email;

                email = emailUser.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotActivity.this, "Favor entrar com um e-mail", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotActivity.this, "E-mail enviado com sucesso!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
            }
        });

    }
}
