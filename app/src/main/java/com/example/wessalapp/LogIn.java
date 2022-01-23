package com.example.wessalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogIn extends AppCompatActivity {


    EditText email, password;
    Button loginBtn, Register_bt;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String email_tx, passwors_tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);

        loginBtn = findViewById(R.id.loginBtn);
        Register_bt = findViewById(R.id.Register_bt);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        loginBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                email_tx = email.getText().toString();
                passwors_tx = password.getText().toString();


                if (email_tx.isEmpty()) {
                    email.setError("Email is Required");
                    return;
                }

                if (passwors_tx.isEmpty()) {
                    email.setError("Password is Required");
                    return;
                }


                fAuth.signInWithEmailAndPassword(email_tx, passwors_tx)

                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {


                                Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();

                                FirebaseUser user = authResult.getUser();

                                DocumentReference user_ref = fStore.collection("Users").document(user.getUid());

                                user_ref.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Check your Email and password and try again", Toast.LENGTH_SHORT).show();

                            }
                        });



            }
        });


        Register_bt.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });


    }
}