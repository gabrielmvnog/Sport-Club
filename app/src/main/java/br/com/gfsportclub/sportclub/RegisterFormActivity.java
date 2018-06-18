package br.com.gfsportclub.sportclub;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class RegisterFormActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private Spinner categoria, genero;
    private static final String[] generos = {"Masculino", "Feminino"};
    private static final String[] categorias = {"Profissional", "Intermediário", "Amador"};
    private int DIALOG_ID = 10;
    private EditText edtDate, edtNome;
    private int year_x, month_x, day_x;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Button finalizar;
    private ImageView edtImg;
    private Uri uri = null;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        final Calendar calendar = Calendar.getInstance();

        day_x = calendar.get(Calendar.DAY_OF_MONTH);
        month_x = calendar.get(Calendar.MONTH);
        year_x = calendar.get(Calendar.YEAR);

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        categoria = (Spinner) findViewById(R.id.reg_form_categoria);
        genero = (Spinner) findViewById(R.id.reg_form_genero);
        edtDate = (EditText) findViewById(R.id.reg_form_data_nasc);
        finalizar = (Button) findViewById(R.id.reg_form_button);
        edtNome = (EditText) findViewById(R.id.reg_form_nome);
        edtImg = (ImageView) findViewById(R.id.reg_form_img);


        ArrayAdapter<String> adapterGenero = new ArrayAdapter<>(RegisterFormActivity.this, android.R.layout.simple_spinner_dropdown_item, generos);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(RegisterFormActivity.this, android.R.layout.simple_spinner_dropdown_item, categorias);

        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoria.setAdapter(adapterCategoria);
        genero.setAdapter(adapterGenero);

        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        genero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
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
                dateUser = edtDate.getText().toString().trim();
                nomeUser = edtNome.getText().toString().trim();

                if(TextUtils.isEmpty(nomeUser)){
                    Toast.makeText(getApplicationContext(),"Entre com um nome!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(dateUser)){
                    Toast.makeText(getApplicationContext(),"Entre com uma data de nascimento!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!TextUtils.isEmpty(dateUser) && !TextUtils.isEmpty(nomeUser)){
                    if(uri == null){
                        mDatabase.child(user.getUid()).child("datanasc").setValue(edtDate.getText().toString());
                        mDatabase.child(user.getUid()).child("nome").setValue(edtNome.getText().toString().trim());
                        mDatabase.child(user.getUid()).child("genero").setValue(genero.getSelectedItem().toString());
                        mDatabase.child(user.getUid()).child("categoria").setValue(categoria.getSelectedItem().toString());
                        mDatabase.child(user.getUid()).child("key").setValue(user.getUid());
                        startActivity(new Intent(RegisterFormActivity.this, SelectSportActivity.class));
                    }else {
                        StorageReference filePath = storageReference.child("PostImage").child(uri.getLastPathSegment());
                        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadurl = taskSnapshot.getDownloadUrl();
                                mDatabase.child(user.getUid()).child("datanasc").setValue(edtDate.getText().toString());
                                mDatabase.child(user.getUid()).child("nome").setValue(edtNome.getText().toString().trim());
                                mDatabase.child(user.getUid()).child("genero").setValue(genero.getSelectedItem().toString());
                                mDatabase.child(user.getUid()).child("categoria").setValue(categoria.getSelectedItem().toString());
                                mDatabase.child(user.getUid()).child("key").setValue(user.getUid());
                                mDatabase.child(user.getUid()).child("imagem").setValue(downloadurl.toString());
                                startActivity(new Intent(RegisterFormActivity.this, SelectSportActivity.class));
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Preencha os campos acima.", Toast.LENGTH_SHORT);
                }


            }
        });

        edtImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent(Intent.ACTION_PICK);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, SELECT_PICTURE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            uri = data.getData();
            edtImg.setImageURI(uri);
        }
    }

}
