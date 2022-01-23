package com.example.wessalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {



    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseUser user = fAuth.getCurrentUser();

        if (user == null) {

            startActivity(new Intent(getApplicationContext(), LogIn.class));

        } else {

        DocumentReference user_ref = fStore.collection("Users").document(user.getUid());

        user_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("Acount_type").equals("Faculty member")) {

                    startActivity(new Intent(getApplicationContext(), FacultymemberHomePage.class));

                }
                if (documentSnapshot.getString("Acount_type").equals("Student")) {

                    startActivity(new Intent(getApplicationContext(), StudentHomePage.class));

                }

            }
        });


    }
    }
}