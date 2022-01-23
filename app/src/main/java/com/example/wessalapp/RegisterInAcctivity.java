package com.example.wessalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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

public class RegisterInAcctivity extends AppCompatActivity {


    TextView activity_description,reg_activity_name;

    ImageView imageview;

    Button Rigester_in_activityBtn;

    String activityid;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_in_acctivity);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            activityid = extras.getString("activity_id");

        }


        imageview = findViewById(R.id.activityimg);

        activity_description = findViewById(R.id.activity_description2);
        reg_activity_name=findViewById(R.id.reg_activity_name);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        fStore.collection("Activity").document(activityid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        reg_activity_name.setText(documentSnapshot.getString("Activity_name"));
                        activity_description.setText(documentSnapshot.getString("Activity_description"));
                        activity_description.setMovementMethod(new ScrollingMovementMethod());


                        storageReference.child("images/" + activityid).getDownloadUrl()
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


        Rigester_in_activityBtn = findViewById(R.id.Rigester_in_activityBtn);
        Rigester_in_activityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                fStore.collection("Activity").document(activityid).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {


                        long required_number= (long) documentSnapshot.get("required_number");
                        long register_number= (long) documentSnapshot.get("register_number");

                               if(register_number<required_number)
                               {


                                   DocumentReference activity_ref = fStore.collection("Activity").document(activityid);


                                   Map<String, Object> activitydata = new HashMap<>();
                                   activitydata.put("register_number ",String.valueOf(register_number+1) );
                                   activitydata.put(fAuth.getCurrentUser().getUid(), "ok");


                                   activity_ref.update(activitydata).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void unused) {

                                           Toast.makeText(getApplicationContext(), " Registaration Done ", Toast.LENGTH_LONG).show();
                                           startActivity(new Intent(getApplicationContext(), StudentHomePage.class));
                                           finish();
                                       }
                                   });


                               }
                               else
                               {

                                   Toast.makeText(getApplicationContext(), " The activity is no longer available.", Toast.LENGTH_LONG).show();


                               }
                            }
                        });






            }
        });




    }
}