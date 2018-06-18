package br.com.gfsportclub.sportclub;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    private static final String[] sports = {"Futebol", "Basquete", "Tênis", "Volei", "Handebol", "Polo Aquático", "Polo"};
    private Uri uri = null;
    private EditText edtName, edtDesc, edtDateI, edtEnd, edtHourI;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private static final int DIALOG_ID_I = 10;
    private static final int DIALOG_ID_HI = 30;
    private int year_x, month_x, day_x, hour_x, minute_x;
    private int PLACE_PICKER_REQUEST = 1;
    private String nomeLocal, endLocal, latLngLocal;
    private Spinner sportSelect;
    final Calendar calendar = Calendar.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        key = getIntent().getStringExtra("EVENT_KEY");

        day_x = calendar.get(Calendar.DAY_OF_MONTH);
        month_x = calendar.get(Calendar.MONTH);
        year_x = calendar.get(Calendar.YEAR);
        hour_x = calendar.get(Calendar.HOUR);
        minute_x = calendar.get(Calendar.MINUTE);

        edtName = findViewById(R.id.nomeEvento);
        edtDesc = findViewById(R.id.descrEvento);
        edtEnd = findViewById(R.id.local_evento);
        edtDateI = findViewById(R.id.data_evento_i);
        edtHourI = findViewById(R.id.hora_evento_i);
        sportSelect = (Spinner) findViewById(R.id.post_sport_type);


        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");

        ArrayAdapter<String> adapterSports = new ArrayAdapter<>(PostActivity.this, android.R.layout.simple_spinner_dropdown_item, sports);
        adapterSports.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSelect.setAdapter(adapterSports);

        edtDateI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID_I);
            }
        });


        edtHourI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID_HI);
            }
        });


        edtEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(PostActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });


        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID_I)
            return new DatePickerDialog(this, dpickerListnerI, year_x, month_x, day_x);

        if (id == DIALOG_ID_HI)
            return new TimePickerDialog(this, tpickerListnerI, hour_x, minute_x, true);

        return null;
    }

    private TimePickerDialog.OnTimeSetListener tpickerListnerI = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;

            calendar.set(Calendar.HOUR_OF_DAY, hour_x);
            calendar.set(Calendar.MINUTE, minute_x);

            edtHourI.setText(String.format("%02d:%02d",hour_x , minute_x));

        }
    };


    private DatePickerDialog.OnDateSetListener dpickerListnerI = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month;
            day_x = dayOfMonth;

            calendar.set(Calendar.DAY_OF_MONTH, day_x);
            calendar.set(Calendar.MONTH, month_x);
            calendar.set(Calendar.YEAR, year_x);

            edtDateI.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(PostActivity.this, data);
                edtEnd.setText(place.getName());
                latLngLocal = place.getLatLng().toString();
                endLocal = place.getAddress().toString();
                nomeLocal = place.getName().toString();
            }
        }

    }

    public void submitButtonClicked(View view) {

        final String nameValue = edtName.getText().toString().trim();
        final String descrValue = edtDesc.getText().toString().trim();
        final String dataValue = edtDateI.getText().toString().trim();
        final String hourValue = edtHourI.getText().toString().trim();


        if (!TextUtils.isEmpty(nameValue)) {

            Toast.makeText(PostActivity.this, "Upload completado", Toast.LENGTH_LONG).show();
            DatabaseReference newPost = databaseReference.child("events").push();
            String key = newPost.getKey();
            newPost.child("titulo").setValue(nameValue);
            newPost.child("descr").setValue(descrValue);
            newPost.child("data").setValue(dataValue);
            newPost.child("hora").setValue(hourValue);
            newPost.child("nomeLocal").setValue(nomeLocal);
            newPost.child("latLngLocal").setValue(latLngLocal);
            newPost.child("endLocal").setValue(endLocal);
            newPost.child("key").setValue(newPost.getKey());
            newPost.child("timestamp").setValue(calendar.getTimeInMillis());
            newPost.child("esporte").setValue(sportSelect.getSelectedItem().toString().toLowerCase());
            newPost.child("adm").setValue(user.getUid());

            databaseReference.child("sports").child(sportSelect.getSelectedItem().toString().toLowerCase()).child("events").child(key).setValue("true");

            startActivity(new Intent(PostActivity.this, BNActivity.class));

        }
    }

}
