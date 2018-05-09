package br.com.gfsportclub.sportclub;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private Uri uri = null;
    private ImageButton imageButton;
    private EditText edtName, edtDesc, edtDateI, edtEnd, edtDateT, edtHourI, edtHourT;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private static final int DIALOG_ID_I = 10;
    private static final int DIALOG_ID_T = 20;
    private static final int DIALOG_ID_HI = 30;
    private static final int DIALOG_ID_HT = 40;
    private int year_x, month_x, day_x, hour_x, minute_x;
    private int PLACE_PICKER_REQUEST = 1;
    private String nomeLocal, endLocal, latLngLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Calendar calendar = Calendar.getInstance();

        day_x = calendar.get(Calendar.DAY_OF_MONTH);
        month_x = calendar.get(Calendar.MONTH);
        year_x = calendar.get(Calendar.YEAR);

        hour_x = calendar.get(Calendar.HOUR);
        minute_x = calendar.get(Calendar.MINUTE);

        edtName = findViewById(R.id.nomeEvento);
        edtDesc = findViewById(R.id.descrEvento);
        edtEnd = findViewById(R.id.local_evento);
        edtDateI = findViewById(R.id.data_evento_i);
        edtDateT = findViewById(R.id.data_evento_t);
        edtHourI = findViewById(R.id.hora_evento_i);
        edtHourT = findViewById(R.id.hora_evento_t);

        edtDateI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID_I);
            }
        });

        edtDateT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(DIALOG_ID_T);
            }
        });

        edtHourI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID_HI);
            }
        });

        edtHourT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(DIALOG_ID_HT);

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
        databaseReference = FirebaseDatabase.getInstance().getReference().child("events");
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID_I)
            return new DatePickerDialog(this, dpickerListnerI, year_x, month_x, day_x);

        if (id == DIALOG_ID_T)
            return new DatePickerDialog(this, dpickerListnerT, year_x, month_x, day_x);

        if (id == DIALOG_ID_HI)
            return new TimePickerDialog(this, tpickerListnerI, hour_x, minute_x, true);

        if (id == DIALOG_ID_HT)
            return new TimePickerDialog(this, tpickerListnerT, hour_x, minute_x, true);


        return null;
    }

    private TimePickerDialog.OnTimeSetListener tpickerListnerT = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;
            edtHourT.setText(hour_x + ":" + minute_x);

        }
    };

    private TimePickerDialog.OnTimeSetListener tpickerListnerI = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;
            edtHourI.setText(hour_x + ":" + minute_x);

        }
    };


    private DatePickerDialog.OnDateSetListener dpickerListnerT = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;
            edtDateT.setText(day_x + "/" + month_x + "/" + year_x);
        }
    };

    private DatePickerDialog.OnDateSetListener dpickerListnerI = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;
            edtDateI.setText(day_x + "/" + month_x + "/" + year_x);
        }
    };

    public void imageButtonClicked(View view){
        Intent galleryintent = new Intent(Intent.ACTION_PICK);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            uri = data.getData();
            imageButton = (ImageButton) findViewById(R.id.addpostimage);
            imageButton.setImageURI(uri);
        }

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

    public void submitButtonClicked(View view){

        final String nameValue = edtName.getText().toString().trim();
        final String descrValue = edtDesc.getText().toString().trim();
        final String dataValue = edtDateI.getText().toString().trim();
        final String endValue = edtEnd.getText().toString().trim();

        if (!TextUtils.isEmpty(nameValue)){
            StorageReference filePath = storageReference.child("PostImage").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     Uri downloadurl = taskSnapshot.getDownloadUrl();
                     Toast.makeText(PostActivity.this, "Upload completado", Toast.LENGTH_LONG).show();
                     DatabaseReference newPost = databaseReference.push();
                     newPost.child("titulo").setValue(nameValue);
                     newPost.child("descr").setValue(descrValue);
                     newPost.child("data").setValue(dataValue);
                     newPost.child("local").child("nomeLocal").setValue(nomeLocal);
                     newPost.child("local").child("latLngLocal").setValue(latLngLocal);
                     newPost.child("local").child("endLocal").setValue(endLocal);
                     newPost.child("imagem").setValue(downloadurl.toString());
                     newPost.child("key").setValue(newPost.getKey());
                }
            });

        } else {
            Toast.makeText(PostActivity.this, "Entre com o titulo do evento", Toast.LENGTH_LONG).show();

        }

    }
}
