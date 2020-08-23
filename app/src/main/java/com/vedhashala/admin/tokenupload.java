package com.vedhashala.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class tokenupload extends AppCompatActivity {
        Button sub,del;
        Spinner spinner;
        EditText tok;
    ArrayList<String> timetok = new ArrayList<>();
    ArrayList<String> tokenlist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tokenupload);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("token");
        tok = findViewById(R.id.tokenedittext);
        sub = findViewById(R.id.tokensub);
        spinner = findViewById(R.id.spinnertok);
        del = findViewById(R.id.tokdel);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = spinner.getSelectedItemPosition();
                myRef.child(timetok.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(),Delete.class);
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
        final int[] count = {0};
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot keynode : dataSnapshot.getChildren()) {
                    String time = keynode.getKey();
                    String token = keynode.child("token").getValue().toString();
                    count[0]++;
                    if (time.length() !=0 && token.length()!= 0){
                        timetok.add(time);
                        tokenlist.add(token);
                    }
                    if (count[0]==dataSnapshot.getChildrenCount()){
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, tokenlist);
                        spinner.setAdapter(arrayAdapter);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                progressDialog.dismiss();
            }
        });
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy :hh:mm:ss");
                Date date = new Date();
                String newdate = dateFormat.format(date).toString();
                myRef.child(newdate).child("token").setValue(tok.getText().toString());
                Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),Delete.class);
                startActivity(i);
                finish();
            }
        });

    }
}