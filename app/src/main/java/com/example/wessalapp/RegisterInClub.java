package com.example.wessalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegisterInClub extends AppCompatActivity {


    TextView club_descrip, reg_club_name;

    ImageView imageview;

    Button Rigester_in_clubBtn;

    String clubid;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_in_club);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clubid = extras.getString("club_id");

        }


        imageview = findViewById(R.id.clubimg);

        club_descrip = findViewById(R.id.club_description2);
        reg_club_name = findViewById(R.id.reg_club_name);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        fStore.collection("Clubs").document(clubid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        reg_club_name.setText(documentSnapshot.getString("Club_name"));
                        club_descrip.setText(documentSnapshot.getString("Club_description"));
                        club_descrip.setMovementMethod(new ScrollingMovementMethod());


                        storageReference.child("images/" + clubid).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(getApplicationContext())
                                                .load(uri)
                                                .into(imageview);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {

                                    }
                                });

                    }
                });


        Rigester_in_clubBtn = findViewById(R.id.Rigester_in_clubBtn);
        Rigester_in_clubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference club_ref = fStore.collection("Clubs").document(clubid);

                Map<String, Object> clubdata = new HashMap<>();
                clubdata.put(fAuth.getCurrentUser().getUid(), "ok");


                club_ref.update(clubdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(getApplicationContext(), " Registaration Done  ", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), StudentHomePage.class));
                        finish();
                    }
                });


            }
        });
    }
}