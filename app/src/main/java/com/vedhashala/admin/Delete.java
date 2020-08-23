package com.vedhashala.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Delete extends AppCompatActivity {
    Button delete, linkbutton;
    TextView caption;
    ImageView imageView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("blog");
    ArrayList<String> timelist = new ArrayList<>();
    ArrayList<String> titlelist = new ArrayList<>();
    ArrayList<String> caplist = new ArrayList<>();
    ArrayList<String> linklist = new ArrayList<>();
    private Bitmap my_image;
    StorageReference ref;
    Uri filePath;
    FirebaseStorage storage;
    StorageReference storageRef;
    Vibrator vib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        caption = findViewById(R.id.captiondelete);
        linkbutton = findViewById(R.id.linkbutton);
        imageView = findViewById(R.id.imageView2);
        delete = findViewById(R.id.deletebutton);
        storage = FirebaseStorage.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getApplicationContext(),tokenupload.class);
                startActivity(intent);
                return false;
            }
        });
        final int[] count = {0};
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot keynode : dataSnapshot.getChildren()) {
                    String time = keynode.getKey();
                    String title = keynode.child("title").getValue().toString();
                    String cap = keynode.child("caption").getValue().toString();
                    String link = keynode.child("link").getValue().toString();
                    count[0]++;
                    if (time.length() !=0 && title.length()!= 0 && cap.length()!= 0 && link.length()!=0){
                        timelist.add(time);
                        titlelist.add(title);
                        caplist.add(cap);
                        linklist.add(link);
                    }
                    if (count[0]==dataSnapshot.getChildrenCount()){
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, titlelist);
                        spinner.setAdapter(arrayAdapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                position = spinner.getSelectedItemPosition();
                                caption.setText(caplist.get(position));
                                ref = storageRef.child(timelist.get(position)).child("image");
                                File localFile = null;
                                try {
                                    localFile = File.createTempFile("images", "jpeg");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                File finalLocalFile = localFile;
                                final File finalLocalFile2 = finalLocalFile;
                                ref.getFile(localFile)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                my_image = BitmapFactory.decodeFile(finalLocalFile2.getAbsolutePath());
                                                imageView.setImageBitmap(my_image);
                                                my_image.equals(null);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle failed download
                                        // ...
                                    }

                                });
                            }
                            @Override
                            public void onNothingSelected(AdapterView <?> parent) {
                            }
                        });
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                progressDialog.dismiss();
            }
        });


        linkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = spinner.getSelectedItemPosition();
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                WebView wv = new WebView(v.getContext());
                wv.loadUrl(linklist.get(position));
                WebSettings webSettings = wv.getSettings();
                webSettings.setJavaScriptEnabled(true);
                wv.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });

                alert.setView(wv);
                alert.show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = spinner.getSelectedItemPosition();
                myRef.child(timelist.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
