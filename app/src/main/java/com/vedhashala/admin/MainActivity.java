package com.vedhashala.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText title , link , caption;
    ImageView image;
    Button submit;
    private final int PICK_IMAGE_REQUEST = 22;
    Uri filePath;
    StorageReference ref;
    FirebaseStorage storage;
    StorageReference storageRef;
    String newdate;
    Bitmap bitmap;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("blog");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        title = findViewById(R.id.editText);
        caption = findViewById(R.id.editText2);
        link = findViewById(R.id.editText3);
        image = findViewById(R.id.imageView);
        submit = findViewById(R.id.button);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().length() !=0 && caption.getText().length() !=0 && link.getText().length() !=0 &&filePath!=null) {

                    Upload();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Invalid input(s)",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void Upload() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy :hh:mm:ss");
        Date date = new Date();
        newdate = dateFormat.format(date).toString();
        myRef.child(newdate).child("title").setValue(title.getText().toString());
        myRef.child(newdate).child("caption").setValue(caption.getText().toString());
        myRef.child(newdate).child("link").setValue(link.getText().toString());

        ref = storageRef.child(newdate).child("image");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] data = baos.toByteArray();
//        Toast.makeText(getApplicationContext(),newdate.toString(),Toast.LENGTH_LONG).show();
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),ref.toString(),Toast.LENGTH_SHORT).show();

                        progressDialog.cancel();
                    }
                });

    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);

                //Setting image to ImageView
                image.setImageBitmap(bitmap);



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}