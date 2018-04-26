package br.com.gfsportclub.sportclub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private Uri uri = null;
    private ImageButton imageButton;
    private EditText edtName, edtDesc, edtData, edtEnd;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        edtName = findViewById(R.id.nomeEvento);
        edtDesc = findViewById(R.id.descrEvento);
        edtData = findViewById(R.id.data_evento);
        edtEnd = findViewById(R.id.local_evento);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("events");
    }

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

    }

    public void submitButtonClicked(View view){

        final String nameValue = edtName.getText().toString().trim();
        final String descrValue = edtDesc.getText().toString().trim();
        final String dataValue = edtData.getText().toString().trim();
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
                     newPost.child("local").setValue(endValue);
                     newPost.child("imagem").setValue(downloadurl.toString());
                     newPost.child("key").setValue(newPost.getKey());
                }
            });

        } else {
            Toast.makeText(PostActivity.this, "Entre com o titulo do evento", Toast.LENGTH_LONG).show();

        }

    }
}
